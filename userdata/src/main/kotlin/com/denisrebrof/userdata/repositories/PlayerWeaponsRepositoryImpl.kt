package com.denisrebrof.userdata.repositories

import com.denisrebrof.userdata.internal.UserDataRepository
import com.denisrebrof.userdata.internal.UserWeaponStatesRepository
import com.denisrebrof.userdata.model.UserWeaponState
import com.denisrebrof.weapons.domain.model.DefaultWeaponIds
import com.denisrebrof.weapons.domain.repositories.IPlayerWeaponsRepository
import com.denisrebrof.weapons.domain.model.PlayerWeaponInfo
import com.denisrebrof.weapons.domain.model.WeaponSlot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerWeaponsRepositoryImpl @Autowired constructor(
    private val userDataRepository: UserDataRepository,
    private val weaponStatesRepository: UserWeaponStatesRepository
) : IPlayerWeaponsRepository {

    override fun getUserWeapons(userId: Long): List<PlayerWeaponInfo> = weaponStatesRepository
        .findByUserId(userId)
        .map(UserWeaponState::toPlayerWeaponInfo)
        .appendDefaultWeapons(userId)

    override fun getWeaponSlot(userId: Long, slot: WeaponSlot): PlayerWeaponInfo? {
        val userData = userDataRepository
            .findUserDataById(userId)
            ?: return null

        val weaponId = when (slot) {
            WeaponSlot.Primary -> userData.primaryWeapon
            WeaponSlot.Secondary -> userData.secondaryWeapon
        }

        return weaponStatesRepository
            .findByUserIdAndWeaponId(userId, weaponId)
            ?.toPlayerWeaponInfo()
            ?: createAndSaveWeaponInfo(userId, weaponId)
    }

    @Transactional
    override fun setWeaponSlot(userId: Long, slot: WeaponSlot, weaponId: Long) {
        val setter = when (slot) {
            WeaponSlot.Primary -> userDataRepository::setPrimaryWeapon
            WeaponSlot.Secondary -> userDataRepository::setSecondaryWeapon
        }
        setter(userId, weaponId)
    }

    override fun getWeapon(
        userId: Long,
        weaponId: Long
    ): PlayerWeaponInfo? = weaponStatesRepository
        .findByUserIdAndWeaponId(userId, weaponId)
        ?.toPlayerWeaponInfo()
        ?: when {
            DefaultWeaponIds.Ids.contains(weaponId) -> createAndSaveWeaponInfo(userId, weaponId)
            else -> null
        }

    override fun setWeapon(userId: Long, state: PlayerWeaponInfo) {
        val newState = weaponStatesRepository
            .findByUserIdAndWeaponId(userId, state.weaponId)
            ?.copy(level = state.weaponLevel)
            ?: UserWeaponState.fromPlayerWeaponInfo(userId, state)

        weaponStatesRepository.save(newState)
    }

    private fun List<PlayerWeaponInfo>.appendDefaultWeapons(
        userId: Long
    ): List<PlayerWeaponInfo> = this
        .appendDefaultWeapon(userId, WeaponSlot.Primary)
        .appendDefaultWeapon(userId, WeaponSlot.Secondary)

    private fun List<PlayerWeaponInfo>.appendDefaultWeapon(
        userId: Long,
        slot: WeaponSlot
    ): List<PlayerWeaponInfo> {
        val weaponId = DefaultWeaponIds.slotToIds[slot] ?: return this
        if (any { it.weaponId == weaponId })
            return this

        return createAndSaveWeaponInfo(userId, weaponId).let(this::plus)
    }

    private fun createAndSaveWeaponInfo(
        userId: Long,
        weaponId: Long
    ) = UserWeaponState(weaponId = weaponId, userId = userId)
        .also(weaponStatesRepository::save)
        .toPlayerWeaponInfo()
}