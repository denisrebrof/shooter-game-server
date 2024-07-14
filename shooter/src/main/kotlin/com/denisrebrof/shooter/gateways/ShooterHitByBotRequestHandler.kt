package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.False
import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.True
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.shooter.domain.model.ShooterGameIntents
import com.denisrebrof.shooter.domain.usecases.GetShooterGameUseCase
import com.denisrebrof.user.gateways.WSUserRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterHitByBotRequestHandler @Autowired constructor(
    private val getGameUseCase: GetShooterGameUseCase
) : WSUserRequestHandler<Long>(WSCommand.IntentHitByBot.id) {

    override fun parseData(data: String): Long = data.toLong()

    override fun handleMessage(userId: Long, data: Long): ResponseState = with(data) {
        val game = getGameUseCase
            .get(userId)
            ?: return@with ResponseState.False

        ShooterGameIntents
            .HitByBot(shooterId = data, receiverId = userId)
            .let(game::submit)

        return@with ResponseState.True
    }
}