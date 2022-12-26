package com.denisrebrof.springboottest.tictac.gateways.model

import kotlinx.serialization.Serializable

@Serializable
data class TicTacGameStateResponse(
    private val cellStates: List<Int>,
    private val gameState: Int,
    private val isWinner: Boolean,
    private val gridSize: Int,
    private val isPlayerTurn: Boolean,
    private var opponentNick: String
)