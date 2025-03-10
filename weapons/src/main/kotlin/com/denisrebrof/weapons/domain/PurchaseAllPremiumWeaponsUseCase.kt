package com.denisrebrof.weapons.domain

import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.model.UserIdentityType
import com.denisrebrof.user.domain.repositories.IUserRepository
import com.denisrebrof.weapons.domain.model.PlayerWeaponInfo
import com.denisrebrof.weapons.domain.model.WeaponInfo
import com.denisrebrof.weapons.domain.repositories.IPlayerWeaponsRepository
import com.denisrebrof.weapons.domain.repositories.IWeaponInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PurchaseAllPremiumWeaponsUseCase @Autowired constructor(
    private val playerWeaponsRepository: IPlayerWeaponsRepository,
    private val userRepository: IUserRepository,
    weaponInfoRepository: IWeaponInfoRepository
) {
    private val premiumWeaponsIds = weaponInfoRepository
        .getAll()
        .filter(WeaponInfo::premium)
        .map(WeaponInfo::id)
        .toSet()

    fun purchase(userId: Long) {
        premiumWeaponsIds.forEach { weaponId ->
            val existingWeapon = playerWeaponsRepository.getWeapon(userId, weaponId)
            if (existingWeapon != null)
                return@forEach

            val playerWeaponInfo = PlayerWeaponInfo(weaponId)
            playerWeaponsRepository.setWeapon(userId, playerWeaponInfo)
        }
    }
}