package com.denisrebrof.games

import arrow.optics.Copy
import arrow.optics.copy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.DisposableContainer
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.processors.PublishProcessor

open class MVIGameHandler<STATE : Any, INTENT : Any, ACTION : Any> private constructor(
    initialState: STATE,
    private val composite: CompositeDisposable
) : Disposable by composite, DisposableContainer by composite {

    constructor(initialState: STATE) : this(initialState, CompositeDisposable())

    private val stateProcessor = BehaviorProcessor.createDefault(initialState)
    private val actionProcessor = PublishProcessor.create<ACTION>()

    private var currentState = initialState

    val state: STATE
        get() = currentState

    val stateFlow: Flowable<STATE>
        get() = stateProcessor

    val actions: Flowable<ACTION>
        get() = actionProcessor

    fun submit(intent: INTENT) = onIntentReceived(intent)

    protected open fun onCreateLifecycle(): Disposable = Disposable.disposed()

    protected open fun onIntentReceived(intent: INTENT) = Unit

    protected fun setState(state: STATE) {
        if (isDisposed)
            return

        assignState(state)
    }

    private fun assignState(state: STATE) {
        currentState = state
        stateProcessor.onNext(state)
    }

    protected fun send(action: ACTION) {
        if (isDisposed)
            return

        actionProcessor.onNext(action)
    }

    protected fun STATE.copyAndSet(copy: Copy<STATE>.() -> Unit) = copy(copy).let(::setState)
}