package com.denisrebrof.progression.domain.model

data class UnclaimedLevelData(
    val lastLevel: Int,
    val currentLevel: Int,
    val weaponRewards: List<Long>
)
