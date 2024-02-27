package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.shooter.domain.model.ShooterGameIntents
import com.denisrebrof.shooter.domain.usecases.GetShooterGameUseCase
import com.denisrebrof.shooter.gateways.ShooterSubmitBotsVisibilityRequestHandler.SubmitBotsVisibilityRequestData
import com.denisrebrof.user.gateways.WSUserRequestHandler
import com.denisrebrof.utils.chunkedFixed
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterSubmitBotsVisibilityRequestHandler @Autowired constructor(
    private val getGameUseCase: GetShooterGameUseCase
) : WSUserRequestHandler<SubmitBotsVisibilityRequestData>(WSCommand.IntentSubmitVisibility.id) {

    override fun parseData(data: String): SubmitBotsVisibilityRequestData = Json.decodeFromString(data)

    override fun handleMessage(userId: Long, data: SubmitBotsVisibilityRequestData): ResponseState {
        val game = getGameUseCase
            .get(userId)
            ?: return ResponseState.NoResponse

        game.submit(data.intent)
        return ResponseState.NoResponse
    }

    @kotlinx.serialization.Serializable
    data class SubmitBotsVisibilityRequestData(
        val playersHash: Int,
        val targetPairs: List<Long>
    ) {
        val intent: ShooterGameIntents.SubmitBotsVisibility
            get() = ShooterGameIntents.SubmitBotsVisibility(
                playersHash = playersHash,
                targets = targetPairs
                    .chunkedFixed(2)
                    .associate { (id, targetId) -> id to targetId }

            )
    }
}