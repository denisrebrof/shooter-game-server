package com.denisrebrof.shooter.domain.game.delegates

import arrow.optics.copy
import arrow.optics.dsl.index
import arrow.optics.typeclasses.Index
import com.denisrebrof.shooter.domain.model.*
import com.denisrebrof.shooter.domain.model.TeamPlayingData.Companion.FlagIdleStateId
import kotlin.reflect.safeCast

fun PlayingState.takeFlag(
    playerId: Long
) = copy {
    val player = players[playerId] ?: return@copy
    val team = player.data.team
    val opponentTeam = if (team == PlayerTeam.Red) PlayerTeam.Blue else PlayerTeam.Red;
    val opponentTeamData = teamData[opponentTeam] ?: return@copy
    val flagIsFree = opponentTeamData.flagPlayerId == null
    if (!flagIsFree)
        return@copy

    val playerPlaying = player.dynamicState.let(Playing::class::safeCast) ?: return@copy
    val canGrabFlag = playerPlaying.transform.isClose(opponentTeamData.flagPos, 1f)
    if (!canGrabFlag)
        return@copy

    PlayingState.teamData.index(Index.map(), opponentTeam).flagStateId set playerId
    println("takeFlag ${team} ${opponentTeam}")
}

fun PlayingState.storeFlag(
    playerId: Long
) = copy {
    val player = players[playerId] ?: return@copy
    val playerTeam = player.data.team
    val opponentTeam = if (playerTeam == PlayerTeam.Red) PlayerTeam.Blue else PlayerTeam.Red;
    val opponentTeamData = teamData[opponentTeam] ?: return@copy
    val flagIsCaptured = opponentTeamData.flagPlayerId == playerId
    if (!flagIsCaptured)
        return@copy

    PlayingState.teamData.index(Index.map(), opponentTeam).flagStateId set FlagIdleStateId
    PlayingState.teamData.index(Index.map(), playerTeam).flagsTaken transform { it + 1 }
    println("storeFlag")
}

fun PlayingState.returnFlag(
    playerId: Long,
    settings: ShooterGameSettings
) = copy {
    val player = players[playerId] ?: return@copy
    val playerTeam = player.data.team
    val playerTeamData = teamData[playerTeam] ?: return@copy
    if (!playerTeamData.flagDropped)
        return@copy

    val teamDataOptional = PlayingState.teamData.index(Index.map(), playerTeam)
    teamDataOptional.flagStateId set FlagIdleStateId
    teamDataOptional.flagPos set settings.getDefaultFlagPos(playerTeam)
    println("returnFlag")
}