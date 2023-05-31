package com.denisrebrof.springboottest.fight.domain.model

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.PublishProcessor

data class FightGame(
    val playerStates: Map<Long, PlayerState>,
    val initialState: GameState = GameState.Preparing
) {
    private val playStateDisposable = CompositeDisposable()

    private val eventsProcessor = PublishProcessor.create<FightGameEvent>()

    private var internalState = initialState
    var state: GameState
        get() = internalState
        set(value) {
            internalState = value
            if (value.finished) {
                playStateDisposable.clear()
            }
        }

    fun doUntilFinish(disposable: Disposable) = playStateDisposable.add(disposable)

    fun dispose() = playStateDisposable.clear()
}

sealed class GameState(val id: Int, val finished: Boolean) {
    object Preparing : GameState(1, false)
    object Playing : GameState(2, false)
    data class HasWinner(val winnerId: Long) : GameState(3, true)
    object Draw : GameState(4, true)
}
