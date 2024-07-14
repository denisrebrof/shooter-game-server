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
class ShooterFlagActionRequestHandler @Autowired constructor(
    private val getGameUseCase: GetShooterGameUseCase
) : WSUserRequestHandler<Long>(WSCommand.IntentFlagAction.id) {
    override fun parseData(data: String): Long = data.toLong()

    override fun handleMessage(userId: Long, data: Long): ResponseState = with(data) {
        val action = FlagActions
            .getAction(data)
            ?: return ResponseState.False

        val game = getGameUseCase
            .get(userId)
            ?: return@with ResponseState.False

        val intent = when (action) {
            FlagActions.TakeFlag -> ShooterGameIntents.TakeFlag(userId)
            FlagActions.StoreFlag -> ShooterGameIntents.StoreFlag(userId)
            FlagActions.ReturnFlag -> ShooterGameIntents.ReturnFlag(userId)
        }

        game.submit(intent)
        return@with ResponseState.True
    }

    private enum class FlagActions(val id: Long) {
        TakeFlag(0),
        StoreFlag(1),
        ReturnFlag(2);

        companion object {
            fun getAction(code: Long) = values().find { it.id == code }
        }
    }
}