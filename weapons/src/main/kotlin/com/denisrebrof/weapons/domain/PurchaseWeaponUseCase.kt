package com.denisrebrof.weapons.domain

import com.denisrebrof.balance.domain.DecreaseBalanceUseCase
import com.denisrebrof.balance.domain.model.CurrencyType
import com.denisrebrof.progression.domain.repositories.IUserProgressionRepository
import com.denisrebrof.weapons.domain.model.PlayerWeaponInfo
import com.denisrebrof.weapons.domain.repositories.IPlayerWeaponsRepository
import com.denisrebrof.weapons.domain.repositories.IWeaponInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PurchaseWeaponUseCase @Autowired constructor(
    private val decreaseBalanceUseCase: DecreaseBalanceUseCase,
    private val playerWeaponsRepository: IPlayerWeaponsRepository,
    private val weaponInfoRepository: IWeaponInfoRepository,
    private val progressionRepository: IUserProgressionRepository
) {
    fun purchase(userId: Long, weaponId: Long): Boolean {
        val existingWeapon = playerWeaponsRepository.getWeapon(userId, weaponId)
        if (existingWeapon != null)
            return false

        val weaponInfo = weaponInfoRepository
            .get(weaponId)
            ?: return false

        val userLevel = progressionRepository
            .getLevel(userId)
            ?: return false

        if (userLevel < weaponInfo.availableFromLevel)
            return false

        val purchaseCost = weaponInfo.settingsLevels[0].cost
        val balanceChangeResult = decreaseBalanceUseCase.decrease(userId, purchaseCost, CurrencyType.Primary)
        if (!balanceChangeResult)
            return false

        val playerWeaponInfo = PlayerWeaponInfo(weaponId)
        playerWeaponsRepository.setWeapon(userId, playerWeaponInfo)
        return true
    }

    fun upgrade(userId: Long, weaponId: Long): Boolean {
        val existingWeapon = playerWeaponsRepository
            .getWeapon(userId, weaponId)
            ?: return false

        val weaponInfo = weaponInfoRepository.get(weaponId) ?: return false
        val levels = weaponInfo.settingsLevels
        val nextLevelIndex = existingWeapon.weaponLevel
        if (nextLevelIndex < 1 || levels.lastIndex < nextLevelIndex)
            return false

        val upgradeCost = levels[nextLevelIndex].cost
        val balanceChangeResult = decreaseBalanceUseCase.decrease(userId, upgradeCost, CurrencyType.Primary)
        if (!balanceChangeResult)
            return false

        val newWeaponState = existingWeapon.copy(weaponLevel = nextLevelIndex + 1)
        playerWeaponsRepository.setWeapon(userId, newWeaponState)
        return true
    }
}