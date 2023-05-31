package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.GetCurrentFightUseCase
import com.denisrebrof.springboottest.fight.domain.model.MovementDirection
import com.denisrebrof.springboottest.fight.gateways.model.FightNotFoundResponse
import com.denisrebrof.springboottest.user.gateways.WSUserRequestHandler
import com.denisrebrof.springboottest.user.gateways.model.UserNotFoundResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SetMovementIntentRequestHandler @Autowired constructor(
    private val getCurrentFightUseCase: GetCurrentFightUseCase
) : WSUserRequestHandler<MovementDirection>(WSCommand.SetMovementData.id) {

    override fun parseData(data: String): MovementDirection = MovementDirection
        .values()
        .first { it.code == data.toLong() }

    override fun handleMessage(userId: Long, data: MovementDirection): ResponseState {
        val fight = getCurrentFightUseCase.get(userId) ?: return FightNotFoundResponse
        val playerState = fight.playerStates[userId] ?: return UserNotFoundResponse
        playerState.intents.movement = data
        return ResponseState.NoResponse
    }
}