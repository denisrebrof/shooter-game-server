package com.denisrebrof.progression.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.UserNotFoundDefaultResponse
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.toResponse
import com.denisrebrof.progression.domain.repositories.ILevelDataRepository
import com.denisrebrof.progression.domain.repositories.IUserProgressionRepository
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LevelProgressionRequestHandler @Autowired constructor(
    private val dataRepository: ILevelDataRepository,
    private val userProgressionRepository: IUserProgressionRepository
) : WSUserEmptyRequestHandler(WSCommand.LevelProgression.id) {

    override fun handleMessage(userId: Long): ResponseState {
        val userLevel = userProgressionRepository.getLevel(userId) ?: return UserNotFoundDefaultResponse
        val userXp = userProgressionRepository.getXp(userId) ?: return UserNotFoundDefaultResponse
        val nextLevelXp = dataRepository.getLevelData(userLevel + 1).xpToReach
        return LevelProgressionResponse(userLevel, userXp, nextLevelXp).toResponse()
    }

    @Serializable
    data class LevelProgressionResponse(
        val level: Int,
        val xp: Int,
        val nextLevelXp: Int
    )
}