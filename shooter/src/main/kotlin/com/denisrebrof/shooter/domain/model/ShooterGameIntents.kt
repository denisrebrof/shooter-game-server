package com.denisrebrof.shooter.domain.model

import com.denisrebrof.games.Transform


sealed class ShooterGameIntents {
    data class Hit(
        val shooterId: Long,
        val weaponId: Long,
        val damage: Int,
        val hitPos: Transform,
        val receiverId: Long?
    ) : ShooterGameIntents()

    data class Shoot(
        val shooterId: Long,
        val weaponId: Long,
        val direction: Transform
    ) : ShooterGameIntents()

    data class SelectWeapon(
        val playerId: Long,
        val weaponId: Long,
    ) : ShooterGameIntents()

    data class UpdatePos(
        val playerId: Long,
        val pos: Transform,
        val verticalLookAngle: Float,
    ) : ShooterGameIntents()
}