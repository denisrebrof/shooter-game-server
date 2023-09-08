package com.denisrebrof.springboottest.shooter.presentation

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.shooter.ShooterGameService
import com.denisrebrof.springboottest.user.gateways.WSUserRequestHandler
import gameentities.Transform
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.ShooterGameIntents
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterHitRequestHandler @Autowired constructor(
    private val service: ShooterGameService,
    private val matchRepository: IMatchRepository
) : WSUserRequestHandler<ShooterHitRequestHandler.HitRequest>(WSCommand.IntentHit.id) {

    override fun parseData(data: String): HitRequest = Json.decodeFromString(data)

    override fun handleMessage(userId: Long, data: HitRequest): ResponseState = with(data) {
        val matchId = matchRepository.getMatchIdByUserId(userId) ?: return@with ResponseState.NoResponse
        val intent = data.toIntent(userId)
        service.submitIntent(matchId, intent)
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
            weaponId = weaponId,
            hitPos = hitPos,
            receiverId = if(receiverId >= 0) receiverId else null,
            damage = damage
        )
    }
}