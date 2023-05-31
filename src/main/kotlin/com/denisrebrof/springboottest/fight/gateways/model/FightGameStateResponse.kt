package com.denisrebrof.springboottest.fight.gateways.model

import kotlinx.serialization.Serializable

@Serializable
data class FightGameStateResponse(
    val playerState: FightPlayerStateResponse,
    val opponentState: FightPlayerStateResponse,
    val stateCode: Int
)