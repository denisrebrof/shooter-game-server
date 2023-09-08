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
class ShooterPositionRequestHandler @Autowired constructor(
    private val service: ShooterGameService,
    private val matchRepository: IMatchRepository
) : WSUserRequestHandler<ShooterPositionRequestHandler.SubmitPosRequest>(WSCommand.IntentSubmitPosition.id) {

    override fun parseData(data: String): SubmitPosRequest = Json.decodeFromString(data)

    override fun handleMessage(userId: Long, data: SubmitPosRequest): ResponseState = with(data) {
        val matchId = matchRepository.getMatchIdByUserId(userId) ?: return@with ResponseState.NoResponse
        val intent = ShooterGameIntents.UpdatePos(userId, pos, verticalLookAngle)
        service.submitIntent(matchId, intent)
        return@with ResponseState.NoResponse
    }

    @Serializable
    data class SubmitPosRequest(
        val pos: Transform,
        val verticalLookAngle: Float
    )
}