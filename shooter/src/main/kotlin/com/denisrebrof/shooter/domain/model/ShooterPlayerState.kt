package com.denisrebrof.shooter.domain.model

import arrow.optics.optics
import com.denisrebrof.games.Transform

@optics
data class ShooterPlayerState(
    val data: ShooterPlayerData,
    val selectedWeaponId: Long,
    val dynamicState: ShooterDynamicState
) {
    companion object
}

@optics
sealed interface ShooterDynamicState {
    companion object
}

@optics
data class Playing(
    val hp: Int,
    val crouching: Boolean,
    val aiming: Boolean,
    val transform: Transform,
    val verticalLookAngle: Float
) : ShooterDynamicState {
    companion object
}

@optics
data class Killed(
    val killPosition: Transform,
) : ShooterDynamicState {
    companion object
}

@optics
data class ShooterPlayerData(
    val team: PlayerTeam,
    val kills: Int = 0,
    val death: Int = 0
) {
    companion object
}