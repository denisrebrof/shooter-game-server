package com.denisrebrof.games

import arrow.optics.Copy
import arrow.optics.copy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.DisposableContainer
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

open class MVIGameHandler<STATE : Any, INTENT : Any, ACTION : Any> private constructor(
    initialState: STATE,
    private val composite: CompositeDisposable
) : Disposable by composite, DisposableContainer by composite {

    constructor(initialState: STATE) : this(initialState, CompositeDisposable())

    private val stateProcessor = BehaviorProcessor.createDefault(initialState)
    private val actionProcessor = PublishProcessor.create<ACTION>()

    val state: STATE
        get() = stateProcessor.value!!

    val stateFlow: Flowable<STATE>
        get() = stateProcessor

    val actions: Flowable<ACTION>
        get() = actionProcessor

    fun submit(intent: INTENT) = onIntentReceived(intent)

    protected open fun onIntentReceived(intent: INTENT) = Unit

    private val stateUpdateLock = Any()

    protected open fun setState(state: STATE) = synchronized(stateUpdateLock) {
        if (isDisposed)
            return

        assignState(state)
    }

    private fun assignState(state: STATE) {
        stateProcessor.onNext(state)
    }

    protected fun send(action: ACTION) {
        if (isDisposed)
            return

        actionProcessor.onNext(action)
    }

    protected fun <TYPED_STATE : STATE> getTypedState(stateType: KClass<TYPED_STATE>) = stateType.safeCast(state)

    protected fun <TYPED_STATE : STATE> withState(
        stateType: KClass<TYPED_STATE>,
        scope: TYPED_STATE.() -> Unit
    ) = synchronized(stateUpdateLock) {
        stateType
            .safeCast(state)
            ?.let(scope)
            ?: Unit
    }

    protected fun <TYPED_STATE : STATE> updateState(
        stateType: KClass<TYPED_STATE>,
        mutation: (TYPED_STATE) -> TYPED_STATE
    ) = synchronized(stateUpdateLock) {
        stateType
            .safeCast(state)
            ?.let(mutation)
            ?.let(::setState)
            ?: Unit
    }

    protected fun <TYPED_STATE : STATE> mutateState(
        stateType: KClass<TYPED_STATE>,
        mutate: TYPED_STATE.() -> TYPED_STATE
    ) = synchronized(stateUpdateLock) {
        stateType
            .safeCast(state)
            ?.mutate()
            ?.let(::setState)
            ?: Unit
    }

    protected fun <TYPED_STATE : STATE> updateStateCopy(
        stateType: KClass<TYPED_STATE>,
        copyFun: Copy<TYPED_STATE>.(TYPED_STATE) -> Unit
    ) = synchronized(stateUpdateLock) {
        val current = stateType
            .safeCast(state)
            ?: return

        current
            .copy { copyFun(current) }
            .let(::setState)
    }
}