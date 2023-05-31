package com.denisrebrof.springboottest.fight.gateways.model

import com.denisrebrof.springboottest.fight.domain.model.PlayerState
import kotlinx.serialization.Serializable

@Serializable
data class FightPlayerStateResponse(
    val pos: Float,
    val blockDir: Int,
    val movementDir: Int,
) {
    companion object {
        fun PlayerState.toResponseData() = FightPlayerStateResponse(
            pos = state.position,
            blockDir = intents.attackDirection.code.toInt(),
            movementDir = intents.movement.code.toInt()
        )
    }
}