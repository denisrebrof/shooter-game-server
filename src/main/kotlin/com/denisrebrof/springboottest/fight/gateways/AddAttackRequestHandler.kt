package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.AttackUseCase
import com.denisrebrof.springboottest.fight.domain.AttackUseCase.AddAttackResult
import com.denisrebrof.springboottest.fight.domain.AttackUseCase.AddAttackResult.*
import com.denisrebrof.springboottest.fight.domain.model.AttackDirection
import com.denisrebrof.springboottest.fight.gateways.model.FightNotFoundResponse
import com.denisrebrof.springboottest.fight.gateways.model.InvalidFightStateResponse
import com.denisrebrof.springboottest.user.gateways.WSUserRequestHandler
import com.denisrebrof.springboottest.user.gateways.model.UserNotFoundResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AddAttackRequestHandler @Autowired constructor(
    private val attackUseCase: AttackUseCase
) : WSUserRequestHandler<AttackDirection>(WSCommand.AddAttack.id) {

    override fun parseData(data: String): AttackDirection = AttackDirection
        .values()
        .first { it.code == data.toLong() }

    override fun handleMessage(userId: Long, data: AttackDirection): ResponseState = attackUseCase
        .add(userId, data)
        .toResponse()

    private fun AddAttackResult.toResponse(): ResponseState = when (this) {
        Executed -> SuccessResponses.Executed.response
        InvalidPlayerState -> SuccessResponses.InvalidPlayerState.response
        InvalidGameState -> InvalidFightStateResponse
        UserNotFound -> UserNotFoundResponse
        FightNotFound -> FightNotFoundResponse
    }

    private enum class SuccessResponses(val code: Long) {
        Executed(0L),
        InvalidPlayerState(1L);

        val response: ResponseState.CreatedResponse = this.code.toString().let(ResponseState::CreatedResponse)
    }
}