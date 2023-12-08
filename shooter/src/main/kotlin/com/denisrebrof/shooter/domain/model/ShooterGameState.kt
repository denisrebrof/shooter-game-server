package com.denisrebrof.shooter.domain.model

import arrow.optics.optics

@optics
sealed interface ShooterGameState {
    companion object
}

@optics
data class Preparing(
    val pendingPlayers: Map<Long, ShooterPlayerData>,
) : ShooterGameState {
    companion object
}

@optics
data class PlayingState(
    val players: Map<Long, ShooterPlayerState>,
    val redTeamKills: Int = 0,
    val blueTeamKills: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
) : ShooterGameState {
    companion object
}

@optics
data class Finished(
    val finishedPlayers: Map<Long, ShooterPlayerData>,
    val winnerTeam: PlayerTeam,
    val redTeamKills: Int,
    val blueTeamKills: Int
) : ShooterGameState {
    companion object
}

val ShooterGameState.playerIds: Set<Long>
    get() = when (this) {
        is Finished -> finishedPlayers.keys
        is PlayingState -> players.keys
        is Preparing -> pendingPlayers.keys
    }

enum class GameStateTypeResponse(val code: Long) {
    Preparing(1L),
    Playing(2L),
    Finished(3L),
}

val ShooterGameState.responseType: GameStateTypeResponse
    get() = when (this) {
        is Preparing -> GameStateTypeResponse.Preparing
        is PlayingState -> GameStateTypeResponse.Playing
        is Finished -> GameStateTypeResponse.Finished
    }