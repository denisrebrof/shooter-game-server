package com.denisrebrof.springboottest.tictac.gateways.model

import kotlinx.serialization.Serializable

@Serializable
data class TicTacFinishedStateResponse(
    private val finished: Boolean,
    private val isWinner: Boolean,
    private val isDraw: Boolean,
)