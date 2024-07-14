package com.denisrebrof.weapons.gateways

import com.denisrebrof.commands.domain.model.NotificationContent.Companion.toNotificationData
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.user.domain.SendUserNotificationUseCase
import com.denisrebrof.weapons.domain.repositories.IPlayerWeaponsRepository
import com.denisrebrof.weapons.domain.repositories.IWeaponInfoRepository
import com.denisrebrof.weapons.domain.model.WeaponSlot
import com.denisrebrof.weapons.gateways.model.WeaponStateResponseData
import com.denisrebrof.weapons.gateways.model.WeaponStatesResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WeaponStatesResponseDelegate @Autowired constructor(
    private val playerWeaponsRepository: IPlayerWeaponsRepository,
    private val weaponInfoRepository: IWeaponInfoRepository,
    private val userNotificationUseCase: SendUserNotificationUseCase
) {
    fun getWeaponStates(userId: Long): WeaponStatesResponseData? {
        val playerWeaponLevels = playerWeaponsRepository
            .getUserWeapons(userId)
            .associate { it.weaponId to it.weaponLevel }
        return WeaponStatesResponseData(
            weapons = weaponInfoRepository.getAll().map { info ->
                val level = playerWeaponLevels[info.id] ?: 0
                WeaponStateResponseData(info, level)
            },
            primaryId = getEquippedWeaponId(userId, WeaponSlot.Primary) ?: return null,
            secondaryId = getEquippedWeaponId(userId, WeaponSlot.Secondary) ?: return null,
        )
    }

    fun notifyWeaponsStates(userId: Long) = getWeaponStates(userId)?.let { responseData ->
        val notification = responseData.toNotificationData()
        userNotificationUseCase.send(userId, WSCommand.WeaponStates.id, notification)
    }

    private fun getEquippedWeaponId(
        userId: Long,
        slot: WeaponSlot
    ) = playerWeaponsRepository
        .getWeaponSlot(userId, slot)
        ?.weaponId
}