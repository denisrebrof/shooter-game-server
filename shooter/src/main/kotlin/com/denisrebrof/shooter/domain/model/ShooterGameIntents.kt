package com.denisrebrof.shooter.domain.model

import com.denisrebrof.games.Transform


sealed class ShooterGameIntents {
    data class HitByBot(
        val shooterId: Long,
        val receiverId: Long
    ) : ShooterGameIntents()

    data class Hit(
        val shooterId: Long,
        val receiverId: Long,
        val damage: Int,
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

    data class TakeFlag(
        val playerId: Long
    ) : ShooterGameIntents()

    data class StoreFlag(
        val playerId: Long
    ) : ShooterGameIntents()

    data class ReturnFlag(
        val playerId: Long
    ) : ShooterGameIntents()

    data class UpdatePos(
        val playerId: Long,
        val pos: Transform,
        val verticalLookAngle: Float,
        val crouching: Boolean,
        val jumping: Boolean,
    ) : ShooterGameIntents()

    data class SubmitBotsVisibility(
        val playersHash: Int,
        val targets: Map<Long, Long>,
    ) : ShooterGameIntents()
}