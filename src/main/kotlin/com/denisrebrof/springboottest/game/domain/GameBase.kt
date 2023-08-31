package com.denisrebrof.springboottest.game.domain

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import subscribeDefault

abstract class GameBase<GAME_STATE : Enum<GAME_STATE>, INPUT, GAME_EVENT : Any>(
    userIds: Set<Long>,
    initialState: GAME_STATE,
) {

    private val lifecycle = CompositeDisposable()

    private val mPlayers = userIds.toMutableSet()
    protected val players: Set<Long>
        get() = mPlayers

    private val stateProcessor = BehaviorProcessor.createDefault(initialState)

    val stateFlow: Flowable<GAME_STATE> = stateProcessor

    abstract fun submitInput(input: INPUT)
    abstract fun getEvents(): Flowable<GAME_EVENT>

    fun start() = createGameLifecycle()
        .doOnComplete { stop() }
        .subscribeDefault()
        .let(lifecycle::add)

    fun stop() {
        onStop()
        lifecycle.clear()
    }

    fun removePlayer(userId: Long) {
        mPlayers.remove(userId)
        onRemovePlayer(userId)
    }

    protected abstract fun createGameLifecycle(): Completable

    protected open fun onStop() = Unit
    protected open fun onRemovePlayer(userId: Long) = Unit

    protected fun <T> Maybe<T>.thenGoToState(
        state: GAME_STATE,
        action: (T) -> Completable
    ): Completable = this
        .doOnSuccess { stateProcessor.onNext(state) }
        .flatMapCompletable(action)

    protected fun <T, R : Any> Maybe<T>.thenGoToState(
        state: GAME_STATE,
        action: (T) -> Maybe<R>
    ): Maybe<R> = this
        .doOnSuccess { stateProcessor.onNext(state) }
        .flatMap(action)

    protected fun <T, R : Any> Maybe<T>.thenGoToState(
        state: GAME_STATE,
        action: () -> Maybe<R>
    ): Maybe<R> = this
        .doOnSuccess { stateProcessor.onNext(state) }
        .flatMap { action() }
}