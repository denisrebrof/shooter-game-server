package com.denisrebrof.userdata.model

import com.denisrebrof.weapons.domain.model.PlayerWeaponInfo
import javax.persistence.*

@Entity
@Table(name = "userweapons")
data class UserWeaponState(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val weaponId: Long = -1L,
    val userId: Long = -1L,
    val level: Int = 1
) {
    fun toPlayerWeaponInfo() = PlayerWeaponInfo(
        weaponId = weaponId,
        weaponLevel = level
    )

    companion object {
        fun fromPlayerWeaponInfo(
            userId: Long,
            info: PlayerWeaponInfo,
        ) = UserWeaponState(
            weaponId = info.weaponId,
            userId = userId,
            level = info.weaponLevel
        )
    }
}