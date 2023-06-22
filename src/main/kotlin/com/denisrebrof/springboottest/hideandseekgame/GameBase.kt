package com.denisrebrof.springboottest.hideandseekgame

import com.denisrebrof.springboottest.utils.subscribeOnIO
import com.denisrebrof.springboottest.utils.subscribeWithLogError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.BehaviorProcessor

abstract class GameBase<GAME_STATE : Enum<GAME_STATE>>(initialState: GAME_STATE) {

    protected val gameLifecycle = CompositeDisposable()

    private val stateProcessor = BehaviorProcessor.createDefault(initialState)

    val stateFlow: Flowable<GAME_STATE> = stateProcessor

    protected abstract fun createGameLoop(): Completable

    fun start() = createGameLoop()
        .doOnComplete { gameLifecycle.clear() }
        .subscribeOnIO()
        .subscribeWithLogError()
        .let(gameLifecycle::add)

    protected fun <T> Maybe<T>.thenGoToState(
        state: GAME_STATE,
        action: (T) -> Completable
    ): Completable = this
        .doOnSuccess { stateProcessor.onNext(state) }
        .flatMapCompletable(action)

    protected fun <T, R> Maybe<T>.thenGoToState(
        state: GAME_STATE,
        action: (T) -> Maybe<R>
    ): Maybe<R> = this
        .doOnSuccess { stateProcessor.onNext(state) }
        .flatMap(action)

    protected fun <T, R> Maybe<T>.thenGoToState(
        state: GAME_STATE,
        action: () -> Maybe<R>
    ): Maybe<R> = this
        .doOnSuccess { stateProcessor.onNext(state) }
        .flatMap { action() }
}