package com.denisrebrof.weapons.domain.repositories

import com.denisrebrof.weapons.domain.model.WeaponInfo

interface IWeaponInfoRepository {
    fun get(id: Long): WeaponInfo?
    fun getAll(): List<WeaponInfo>
}