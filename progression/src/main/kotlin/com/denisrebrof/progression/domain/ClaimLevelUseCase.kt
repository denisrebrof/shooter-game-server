package com.denisrebrof.progression.domain

import com.denisrebrof.progression.domain.model.UnclaimedLevelData
import com.denisrebrof.progression.domain.repositories.IUserProgressionRepository
import com.denisrebrof.progression.domain.repositories.IWeaponRewardsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClaimLevelUseCase @Autowired constructor(
    private val progressionRepository: IUserProgressionRepository,
    private val weaponRewardsRepository: IWeaponRewardsRepository
) {
    fun getUnclaimedLevelsData(userId: Long): UnclaimedLevelData? = progressionRepository.run {
        val current = getLevel(userId) ?: return null
        val lastClaimed = getLastClaimedLevel(userId) ?: return null
        if (lastClaimed >= current)
            return null

        val rewards = weaponRewardsRepository.getRewards(lastClaimed, current)
        return UnclaimedLevelData(lastClaimed, current, rewards)
    }

    fun claimLastLevel(userId: Long) = progressionRepository.run {
        val current = getLevel(userId) ?: return
        setLastClaimedLevel(userId, current)
    }
}