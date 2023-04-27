package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.UpdateFighterIntentUseCase
import com.denisrebrof.springboottest.fight.domain.model.MovementDirection
import com.denisrebrof.springboottest.user.gateways.WSUserRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SetMovementIntentRequestHandler @Autowired constructor(
    private val updateFighterIntentUseCase: UpdateFighterIntentUseCase
) : WSUserRequestHandler<MovementDirection>(WSCommand.SetMovementData.id) {

    override fun parseData(data: String): MovementDirection = MovementDirection
        .values()
        .first { it.code == data.toLong() }

    override fun handleMessage(userId: Long, data: MovementDirection): ResponseState {
        updateFighterIntentUseCase.update(userId) { intent ->
            intent.copy(movement = data)
        }
        return ResponseState.NoResponse
    }
}