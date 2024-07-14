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
class ShooterHitRequestHandler @Autowired constructor(
    private val getGameUseCase: GetShooterGameUseCase
) : WSUserRequestHandler<ShooterHitRequestHandler.HitRequest>(WSCommand.IntentHit.id) {

    override fun parseData(data: String): HitRequest = Json.decodeFromString(data)

    override fun handleMessage(userId: Long, data: HitRequest): ResponseState = with(data) {
        val game = getGameUseCase.get(userId) ?: return@with ResponseState.NoResponse
        data.toIntent(userId).let(game::submit)
        return@with ResponseState.NoResponse
    }

    @Serializable
    data class HitRequest(
        val weaponId: Long,
        val damage: Int,
        val hitPos: Transform,
        val receiverId: Long
    ) {
        fun toIntent(playerId: Long) = ShooterGameIntents.Hit(
            shooterId = playerId,
            receiverId = receiverId,
            damage = damage
        )
    }
}