package com.denisrebrof.progression.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.progression.domain.ClaimLevelUseCase
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClaimLevelDataRewardsRequestHandler @Autowired constructor(
    private val claimLevelUseCase: ClaimLevelUseCase
) : WSUserEmptyRequestHandler(WSCommand.ClaimLevelRewards.id) {

    override fun handleMessage(userId: Long): ResponseState {
        claimLevelUseCase.claimLastLevel(userId)
        return ResponseState.NoResponse
    }
}