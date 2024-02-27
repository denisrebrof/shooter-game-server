package com.denisrebrof.progression.domain

import com.denisrebrof.progression.domain.model.UnclaimedLevelData
import com.denisrebrof.progression.domain.repositories.ILevelDataRepository
import com.denisrebrof.progression.domain.repositories.IUserProgressionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClaimLevelUseCase @Autowired constructor(
    private val progressionRepository: IUserProgressionRepository,
    private val levelDataRepository: ILevelDataRepository
) {
    fun getUnclaimedLevelsData(userId: Long): UnclaimedLevelData? = progressionRepository.run {
        val current = getLevel(userId) ?: return null
        val lastClaimed = getLastClaimedLevel(userId) ?: return null
        if (lastClaimed >= current)
            return null

        val rewards = (lastClaimed until current)
            .mapNotNull(levelDataRepository::getLevelData)
            .fold("") { acc, data -> acc + " " + data.xpToReach }

        return UnclaimedLevelData(
            lastClaimed,
            current,
            rewards
        )
    }

    fun claimLastLevel(userId: Long) = progressionRepository.run {
        val current = getLevel(userId) ?: return
        setLastClaimedLevel(userId, current)
    }
}