package com.denisrebrof.weapons.domain

import com.denisrebrof.weapons.domain.model.WeaponSlot
import com.denisrebrof.weapons.domain.repositories.IPlayerWeaponsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SetWeaponSlotUseCase @Autowired constructor(
    private val playerWeaponsRepository: IPlayerWeaponsRepository
) {
    fun setWeaponSlot(
        userId: Long,
        weaponId: Long,
        slot: WeaponSlot
    ): Boolean {
        val hasWeapon = playerWeaponsRepository.getWeapon(userId, weaponId) != null
        if (!hasWeapon)
            return false

        playerWeaponsRepository.setWeaponSlot(userId, slot, weaponId)
        return true
    }
}