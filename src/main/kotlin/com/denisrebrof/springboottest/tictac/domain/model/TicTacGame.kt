package com.denisrebrof.springboottest.tictac.domain.model

data class TicTacGame(
    val participantIds: List<Long>,
    val size: Int = 3,
    val userIdToCell: List<Long> = MutableList(size * size) { 0 },
    val state: GameState = participantIds.firstOrNull()?.let(GameState::ActiveTurn) ?: GameState.Undefined
)

sealed class GameState(val id: Int, val finished: Boolean) {
    object Undefined : GameState(0, false)
    data class ActiveTurn(val turnUserId: Long) : GameState(1, false)
    data class HasWinner(val winnerId: Long) : GameState(2, true)
    object Draw : GameState(3, true)
}
