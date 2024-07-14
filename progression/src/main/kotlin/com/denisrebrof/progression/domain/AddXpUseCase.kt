package com.denisrebrof.progression.domain

import com.denisrebrof.progression.domain.repositories.ILevelDataRepository
import com.denisrebrof.progression.domain.repositories.IUserProgressionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AddXpUseCase @Autowired constructor(
    private val progressionRepository: IUserProgressionRepository,
    private val levelDataRepository: ILevelDataRepository
) {
    fun addXp(userId: Long, xp: Int) = progressionRepository.run {
        var userLevel = getLevel(userId) ?: return
        var xpToAdd = getXp(userId)?.plus(xp) ?: return
        var xpToNextLevel = getXpToNextLevel(userLevel)
        while (xpToAdd > xpToNextLevel) {
            xpToAdd -= xpToNextLevel
            userLevel++
            xpToNextLevel = getXpToNextLevel(userLevel)
        }

        setXp(userId, xpToAdd)
        setLevel(userId, userLevel)
    }

    private fun getXpToNextLevel(levelId: Int) = levelDataRepository.getLevelData(levelId + 1).xpToReach
}