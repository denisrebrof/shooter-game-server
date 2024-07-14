package com.denisrebrof.shooter.domain.game.delegates

import arrow.optics.copy
import arrow.optics.dsl.index
import arrow.optics.typeclasses.Index
import com.denisrebrof.shooter.domain.model.*

fun PlayingState.hit(
    shooterId: Long,
    receiverId: Long,
    damage: Int
): HitResult {
    val shooter = PlayingState.getPlayerStateOptional(shooterId)
    val shooterPlaying = shooter.dynamicState.playing
    if (shooterPlaying.isEmpty(this))
        return HitResult(this)

    val receiver = PlayingState.getPlayerStateOptional(receiverId)
    val receiverPlaying = receiver.dynamicState.playing
    val receiverState = receiverPlaying
        .getOrNull(this)
        ?: return HitResult(this)

    val newHp = receiverState.hp - damage
    val hpLoss = receiverState.hp - newHp
    if (newHp > 0) {
        val newState = copy { receiverPlaying.hp set newHp }
        return HitResult(newState, hpLoss)
    }

    val newState = copy {
        shooter.data.kills transform { it + 1 }
        receiver.data.death transform { it + 1 }
        receiver.dynamicState set Killed(receiverState.transform)

        val killerTeam = shooter.data.team
            .getOrNull(this@hit)
            ?: return@copy

        val killerTeamCounter = PlayingState.teamData
            .index(Index.map(), killerTeam)
            .kills

        killerTeamCounter transform { it + 1 }

        val killerTeamDataOptional = PlayingState
            .teamData
            .index(Index.map(), killerTeam)
        val killerTeamData = killerTeamDataOptional
            .getOrNull(this@hit)
            ?: return@copy
        if(killerTeamData.flagPlayerId != receiverId)
            return@copy

        killerTeamDataOptional.flagStateId set TeamPlayingData.FlagDroppedStateId
        killerTeamDataOptional.flagPos set receiverState.transform
        println("dropFlag 5")
    }

    return HitResult(newState, hpLoss, true)
}

data class HitResult(
    val state: PlayingState,
    val hpLoss: Int = 0,
    val killed: Boolean = false
)