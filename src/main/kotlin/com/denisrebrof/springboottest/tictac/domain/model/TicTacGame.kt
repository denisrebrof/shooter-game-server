package com.denisrebrof.springboottest.tictac.domain.model

data class TicTacGame(
    val participantIds: List<Long>,
    val size: Int = 3,
    val userIdToCell: List<Long> = MutableList(size * size) { 0 },
    val state: GameState = participantIds.firstOrNull()?.let(GameState::ActiveTurn) ?: GameState.Undefined
)

sealed class GameState(val id: Int) {
    object Undefined : GameState(0)
    data class ActiveTurn(val turnUserId: Long) : GameState(1)
    data class Finished(val winnerId: Long) : GameState(2)
}
