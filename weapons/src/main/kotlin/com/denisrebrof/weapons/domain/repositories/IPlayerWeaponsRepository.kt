package com.denisrebrof.weapons.domain.repositories

import com.denisrebrof.weapons.domain.model.PlayerWeaponInfo
import com.denisrebrof.weapons.domain.model.WeaponSlot

interface IPlayerWeaponsRepository {
    fun getUserWeapons(userId: Long): List<PlayerWeaponInfo>
    fun getWeaponSlot(userId: Long, slot: WeaponSlot): PlayerWeaponInfo?
    fun setWeaponSlot(userId: Long, slot: WeaponSlot, weaponId: Long)
    fun getWeapon(userId: Long, weaponId: Long): PlayerWeaponInfo?
    fun setWeapon(userId: Long, state: PlayerWeaponInfo)
}