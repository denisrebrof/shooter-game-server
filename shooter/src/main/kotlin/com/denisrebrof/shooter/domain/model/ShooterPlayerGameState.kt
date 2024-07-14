package com.denisrebrof.shooter.domain.model

import com.denisrebrof.weapons.domain.model.PlayerWeapon
import kotlinx.serialization.Serializable

@Serializable
data class ShooterPlayerGameState(
    val gameActive: Boolean,
    val mapId: Int,
    val primaryWeapon: PlayerWeapon,
    val secondaryWeapon: PlayerWeapon,
) {
    companion object {
        val Inactive = ShooterPlayerGameState(
            gameActive = false,
            mapId = 0,
            primaryWeapon = PlayerWeapon.Undefined,
            secondaryWeapon = PlayerWeapon.Undefined
        )
    }
}