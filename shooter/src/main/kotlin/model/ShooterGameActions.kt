package model

import gameentities.Transform
import kotlinx.serialization.Serializable


sealed class ShooterGameActions {

    object LifecycleCompleted: ShooterGameActions()
    @Serializable
    data class Shoot(
        val shooterId: Long,
        val weaponId: Long,
        val hitPos: Transform
    ): ShooterGameActions()
    @Serializable
    data class Hit(
        val damagerId: Long,
        val receiverId: Long,
        val hpLoss: Int,
        val killed: Boolean
    ): ShooterGameActions()
}