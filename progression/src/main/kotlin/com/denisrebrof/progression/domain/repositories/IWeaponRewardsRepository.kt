package com.denisrebrof.progression.domain.repositories

interface IWeaponRewardsRepository {
    fun getRewards(lastClaimedLevel: Int, currentLevel: Int): List<Long>
}