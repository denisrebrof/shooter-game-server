package com.denisrebrof.shooter.domain.model

import arrow.optics.optics

@optics
data class ShooterBotState(
    val playerState: ShooterPlayerState,
    val routeIndex: Int?,
    val routePointIndex: Int?,
) {
    companion object
}