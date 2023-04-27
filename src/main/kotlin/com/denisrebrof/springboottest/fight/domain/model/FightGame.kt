package com.denisrebrof.springboottest.fight.domain.model

data class FightGame(
    val playerStates: Map<Long, PlayerState>,
    val state: GameState = GameState.Preparing
)

data class PlayerState(
    val intents: FighterIntent,
    val state: FighterState
)

sealed class GameState(val id: Int, val finished: Boolean) {
    object Undefined : GameState(0, false)
    object Preparing : GameState(1, false)
    object Playing : GameState(2, false)
    data class HasWinner(val winnerId: Long) : GameState(3, true)
    object Draw : GameState(3, true)
}
