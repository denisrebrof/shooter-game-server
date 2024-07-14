package com.denisrebrof.weapons.data

import com.denisrebrof.progression.domain.repositories.IWeaponRewardsRepository
import com.denisrebrof.weapons.domain.model.WeaponInfo
import com.denisrebrof.weapons.domain.repositories.IWeaponInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WeaponRewardsRepository @Autowired constructor(
    private val weaponsRepository: IWeaponInfoRepository
) : IWeaponRewardsRepository {
    override fun getRewards(lastClaimedLevel: Int, currentLevel: Int): List<Long> {
        val levelsRange = lastClaimedLevel.plus(1)..currentLevel
        return weaponsRepository
            .getAll()
            .filter { it.availableFromLevel in levelsRange }
            .map(WeaponInfo::id)
    }
}