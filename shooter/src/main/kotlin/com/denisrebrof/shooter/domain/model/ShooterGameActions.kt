package com.denisrebrof.shooter.domain.model

import com.denisrebrof.games.Transform
import kotlinx.serialization.Serializable


sealed class ShooterGameActions {
    object LifecycleCompleted: ShooterGameActions()
    @Serializable
    data class Shoot(
        val shooterId: Long,
        val weaponId: Long,
        val direction: Transform
    ): ShooterGameActions()
    @Serializable
    data class Hit(
        val damagerId: Long,
        val receiverId: Long,
        val hpLoss: Int,
        val killed: Boolean
    ): ShooterGameActions()
}