package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.ShooterGameIntents
import com.denisrebrof.shooter.domain.usecases.GetShooterGameUseCase
import com.denisrebrof.shooter.gateways.ShooterPositionRequestHandler.SubmitPosRequest
import com.denisrebrof.user.gateways.WSUserRequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterPositionRequestHandler @Autowired constructor(
    private val getGameUseCase: GetShooterGameUseCase
) : WSUserRequestHandler<SubmitPosRequest>(WSCommand.IntentSubmitPosition.id) {

    override fun parseData(data: String): SubmitPosRequest = Json.decodeFromString(data)

    override fun handleMessage(userId: Long, data: SubmitPosRequest): ResponseState = with(data) {
        val game = getGameUseCase.get(userId) ?: return@with ResponseState.NoResponse
        ShooterGameIntents.UpdatePos(userId, pos, verticalLookAngle, crouching, jumping).let(game::submit)
        return@with ResponseState.NoResponse
    }

    @Serializable
    data class SubmitPosRequest(
        val pos: Transform,
        val verticalLookAngle: Float,
        val crouching: Boolean,
        val jumping: Boolean,
    )
}