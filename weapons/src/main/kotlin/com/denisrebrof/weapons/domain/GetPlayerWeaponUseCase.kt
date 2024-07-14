package com.denisrebrof.weapons.domain

import com.denisrebrof.weapons.domain.model.PlayerWeapon
import com.denisrebrof.weapons.domain.model.PlayerWeapon.Companion.toPlayerWeapon
import com.denisrebrof.weapons.domain.model.WeaponSlot
import com.denisrebrof.weapons.domain.repositories.IPlayerWeaponsRepository
import com.denisrebrof.weapons.domain.repositories.IWeaponInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetPlayerWeaponUseCase @Autowired constructor(
    private val playerWeaponsRepository: IPlayerWeaponsRepository,
    private val weaponInfoRepository: IWeaponInfoRepository
) {
    fun getWeapon(userId: Long, slot: WeaponSlot): PlayerWeapon? {
        val data = playerWeaponsRepository
            .getWeaponSlot(userId, slot)
            ?: return null

        return weaponInfoRepository
            .get(data.weaponId)
            ?.toPlayerWeapon(data.weaponLevel)
    }
}