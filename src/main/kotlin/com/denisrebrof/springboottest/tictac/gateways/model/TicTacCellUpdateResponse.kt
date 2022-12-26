package com.denisrebrof.springboottest.tictac.gateways.model

import kotlinx.serialization.Serializable

@Serializable
data class TicTacCellUpdateResponse(
    val cellPos: Int,
    val isMine: Boolean
)