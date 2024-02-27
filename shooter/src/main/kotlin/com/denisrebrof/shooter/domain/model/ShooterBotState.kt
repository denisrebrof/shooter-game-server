package com.denisrebrof.shooter.domain.model

import arrow.optics.optics

const val emptyBotTargetId = -999L

@optics
data class ShooterBotState(
    val playerState: ShooterPlayerState,
    val routeIndex: Int?,
    val routePointIndex: Int?,
) {
    companion object
}