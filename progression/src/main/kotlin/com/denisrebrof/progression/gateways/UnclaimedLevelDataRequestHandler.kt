package com.denisrebrof.progression.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.UserNotFoundDefaultResponse
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.toResponse
import com.denisrebrof.progression.domain.ClaimLevelUseCase
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UnclaimedLevelDataRequestHandler @Autowired constructor(
    private val claimLevelUseCase: ClaimLevelUseCase
) : WSUserEmptyRequestHandler(WSCommand.UnclaimedLevelRewardsData.id) {

    override fun handleMessage(userId: Long): ResponseState {
        val data = claimLevelUseCase.getUnclaimedLevelsData(userId) ?: return ResponseState.NoResponse
        return UnclaimedLevelRewardsResponse(data.lastLevel, data.currentLevel, data.rewards).toResponse()
    }

    @Serializable
    data class UnclaimedLevelRewardsResponse(
        val lastLevel: Int,
        val currentLevel: Int,
        val rewards: String
    )
}