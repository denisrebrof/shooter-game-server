package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.fromLong
import com.denisrebrof.shooter.domain.model.timeLeft
import com.denisrebrof.shooter.domain.usecases.GetShooterGameUseCase
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterTimeLeftRequestHandler @Autowired constructor(
    private val getGameUseCase: GetShooterGameUseCase
) : WSUserEmptyRequestHandler(WSCommand.TimeLeft.id) {

    private val wrongParamsResponse = ResponseState.fromLong(-1L)

    override fun handleMessage(userId: Long): ResponseState {
        val game = getGameUseCase.get(userId) ?: return wrongParamsResponse
        return ResponseState.fromLong(game.state.timeLeft)
    }
}