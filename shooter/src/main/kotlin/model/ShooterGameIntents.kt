package model

import gameentities.Transform


sealed class ShooterGameIntents {
    data class Shoot(
        val shooterId: Long,
        val weaponId: Long,
        val damage: Int,
        val hitPos: Transform,
        val receiverId: Long?
    ): ShooterGameIntents()
    data class SelectWeapon(
        val playerId: Long,
        val weaponId: Long,
    ): ShooterGameIntents()

    data class UpdatePos(
        val playerId: Long,
        val pos: Transform,
        val verticalLookAngle: Float,
    ): ShooterGameIntents()
}