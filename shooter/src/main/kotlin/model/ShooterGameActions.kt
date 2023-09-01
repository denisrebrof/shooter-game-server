package model

import gameentities.Transform


sealed class ShooterGameActions {

    object LifecycleCompleted: ShooterGameActions()
    data class CanSpawn(
        val playerId: Long
    ): ShooterGameActions()
    data class Spawned(
        val playerId: Long,
        val spawnId: Long
    ): ShooterGameActions()

    data class Shoot(
        val shooterId: Long,
        val weaponId: Long,
        val hitPos: Transform
    ): ShooterGameActions()

    data class Hit(
        val damagerId: Long,
        val receiverId: Long,
        val hpLoss: Int,
        val killed: Boolean
    ): ShooterGameActions()
}