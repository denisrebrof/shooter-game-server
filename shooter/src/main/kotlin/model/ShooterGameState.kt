package model

import arrow.optics.optics
import gameentities.Transform

enum class PlayerTeam(val id: Int) {
    Red(1),
    Blue(2)
}

@optics data class ShooterPlayerState(
    val data: ShooterPlayerData,
    val dynamicState: ShooterDynamicState = Pending
) { companion object }

@optics sealed interface ShooterDynamicState { companion object }

object Pending : ShooterDynamicState
@optics data class Playing(
    val hp: Int,
    val transform: Transform,
    val verticalLookAngle: Float,
    val selectedWeaponId: Long
) : ShooterDynamicState { companion object }

@optics data class Killed(
    val killPosition: Transform
) : ShooterDynamicState { companion object }

@optics data class ShooterPlayerData(
    val team: PlayerTeam,
    val kills: Int = 0,
    val death: Int = 0
) { companion object }

@optics sealed interface ShooterGameState { companion object }
@optics data class Preparing(val pendingPlayers: Map<Long, ShooterPlayerData>): ShooterGameState { companion object }
@optics data class PlayingState(val players: Map<Long, ShooterPlayerState>): ShooterGameState { companion object }
@optics data class Finished(
    val finishedPlayers: Map<Long, ShooterPlayerData>,
    val winnerTeam: PlayerTeam
): ShooterGameState { companion object }

val ShooterGameState.playerIds: Set<Long>
    get() = when(this) {
        is Finished -> finishedPlayers.keys
        is PlayingState -> players.keys
        is Preparing -> pendingPlayers.keys
    }