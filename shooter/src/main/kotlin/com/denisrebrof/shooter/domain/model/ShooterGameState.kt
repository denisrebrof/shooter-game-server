package com.denisrebrof.shooter.domain.model

import arrow.optics.optics
import com.denisrebrof.games.Transform

enum class PlayerTeam(val id: Int) {
    Red(1),
    Blue(2)
}

@optics data class ShooterPlayerState(
    val data: ShooterPlayerData,
    val selectedWeaponId: Long,
    val dynamicState: ShooterDynamicState
) { companion object }

@optics sealed interface ShooterDynamicState { companion object }

@optics data class Playing(
    val hp: Int,
    val transform: Transform,
    val verticalLookAngle: Float
) : ShooterDynamicState { companion object }

@optics data class Killed(
    val killPosition: Transform,
) : ShooterDynamicState { companion object }

@optics data class ShooterPlayerData(
    val team: PlayerTeam,
    val kills: Int = 0,
    val death: Int = 0
) { companion object }

@optics sealed interface ShooterGameState { companion object }
@optics data class Preparing(
    val pendingPlayers: Map<Long, ShooterPlayerData>,
    val gameDuration: Long
): ShooterGameState { companion object }
@optics data class PlayingState(
    val players: Map<Long, ShooterPlayerState>,
    val redTeamKills: Int = 0,
    val blueTeamKills: Int = 0,
    val gameDuration: Long,
    val startTime: Long = System.currentTimeMillis(),
): ShooterGameState { companion object }

@optics data class Finished(
    val finishedPlayers: Map<Long, ShooterPlayerData>,
    val winnerTeam: PlayerTeam,
    val redTeamKills: Int,
    val blueTeamKills: Int
): ShooterGameState { companion object }

val ShooterGameState.playerIds: Set<Long>
    get() = when(this) {
        is Finished -> finishedPlayers.keys
        is PlayingState -> players.keys
        is Preparing -> pendingPlayers.keys
    }

val ShooterGameState.timeLeft: Long
    get() = when (this) {
        is Finished -> 0L
        is PlayingState -> gameDuration + startTime - System.currentTimeMillis()
        is Preparing -> gameDuration
    }