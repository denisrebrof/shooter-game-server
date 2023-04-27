package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.UpdateFighterIntentUseCase
import com.denisrebrof.springboottest.fight.domain.model.AttackDirection
import com.denisrebrof.springboottest.user.gateways.WSUserRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SetAttackDirectionRequestHandler @Autowired constructor(
    private val updateFighterIntentUseCase: UpdateFighterIntentUseCase
) : WSUserRequestHandler<AttackDirection>(WSCommand.SetAttackDirection.id) {

    override fun parseData(data: String): AttackDirection = AttackDirection
        .values()
        .first { it.code == data.toLong() }

    override fun handleMessage(userId: Long, data: AttackDirection): ResponseState {
        updateFighterIntentUseCase.update(userId) { intent ->
            intent.copy(attackDirection = data)
        }
        return ResponseState.NoResponse
    }
}