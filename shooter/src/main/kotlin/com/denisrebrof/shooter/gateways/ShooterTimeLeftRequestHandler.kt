package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.fromLong
import com.denisrebrof.shooter.domain.ShooterGame
import com.denisrebrof.shooter.domain.model.Finished
import com.denisrebrof.shooter.domain.model.PlayingState
import com.denisrebrof.shooter.domain.model.Preparing
import com.denisrebrof.shooter.domain.usecases.GetShooterGameUseCase
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterTimeLeftRequestHandler @Autowired constructor(
    private val getGameUseCase: GetShooterGameUseCase
) : WSUserEmptyRequestHandler(WSCommand.TimeLeft.id) {

    private val wrongParamsResponse = ResponseState.fromLong(-1L)

    private val ShooterGame.timeLeft: Long
        get() = when (val stateSnapshot = state) {
            is Finished -> 0L
            is Preparing -> settings.gameDuration
            is PlayingState -> settings.gameDuration + stateSnapshot.startTime - System.currentTimeMillis()
        }

    override fun handleMessage(userId: Long): ResponseState {
        val game = getGameUseCase.get(userId) ?: return wrongParamsResponse
        return ResponseState.fromLong(game.timeLeft)
    }


}