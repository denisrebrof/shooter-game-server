package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.ShooterGameIntents
import com.denisrebrof.shooter.domain.usecases.GetShooterGameUseCase
import com.denisrebrof.user.gateways.WSUserRequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterShootRequestHandler @Autowired constructor(
    private val getGameUseCase: GetShooterGameUseCase
) : WSUserRequestHandler<ShooterShootRequestHandler.IntentShootData>(WSCommand.IntentShoot.id) {

    override fun parseData(data: String): IntentShootData = Json.decodeFromString(data)

    override fun handleMessage(userId: Long, data: IntentShootData): ResponseState = with(data) {
        val game = getGameUseCase.get(userId) ?: return@with ResponseState.NoResponse
        data.toIntent(userId).let(game::submit)
        return@with ResponseState.NoResponse
    }

    @Serializable
    data class IntentShootData(
        val weaponId: Long,
        val direction: Transform
    ) {
        fun toIntent(playerId: Long) = ShooterGameIntents.Shoot(
            shooterId = playerId,
            weaponId = weaponId,
            direction = direction
        )
    }
}