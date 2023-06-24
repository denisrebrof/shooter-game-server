package com.denisrebrof.springboottest.game.domain

import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.BehaviorProcessor

abstract class GameBase<GAME_STATE : Enum<GAME_STATE>>(initialState: GAME_STATE) {

    protected val lifecycle = CompositeDisposable()

    private val stateProcessor = BehaviorProcessor.createDefault(initialState)

    fun start() = createGameLifecycle()
        .doOnComplete { stop() }
        .subscribeDefault()
        .let(lifecycle::add)

    fun stop() {
        onStop()
        lifecycle.clear()
    }

    protected abstract fun createGameLifecycle(): Completable
    val stateFlow: Flowable<GAME_STATE> = stateProcessor

    protected open fun onStop() = Unit

    protected fun goToState(state: GAME_STATE) = stateProcessor.onNext(state)

    protected fun <T> Maybe<T>.thenGoToState(
        state: GAME_STATE,
        action: (T) -> Completable
    ): Completable = this
        .doOnSuccess { goToState(state) }
        .flatMapCompletable(action)

    protected fun <T, R> Maybe<T>.thenGoToState(
        state: GAME_STATE,
        action: (T) -> Maybe<R>
    ): Maybe<R> = this
        .doOnSuccess { goToState(state) }
        .flatMap(action)

    protected fun <T, R> Maybe<T>.thenGoToState(
        state: GAME_STATE,
        action: () -> Maybe<R>
    ): Maybe<R> = this
        .doOnSuccess { goToState(state) }
        .flatMap { action() }
}