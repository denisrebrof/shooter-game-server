package com.denisrebrof.shooter.domain.game.delegates

import arrow.optics.copy
import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.*

fun PlayingState.updatePlayerPos(
    playerId: Long,
    pos: Transform,
    lookAngle: Float
) = copy {
    PlayingState
        .getPlayerStateOptional(playerId)
        .dynamicState
        .playing
        .run {
            transform set pos
            verticalLookAngle set lookAngle
        }
}
