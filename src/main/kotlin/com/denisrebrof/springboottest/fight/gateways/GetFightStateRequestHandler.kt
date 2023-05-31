package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.GetCurrentFightUseCase
import com.denisrebrof.springboottest.fight.domain.model.FightGame
import com.denisrebrof.springboottest.fight.gateways.model.FightGameStateResponse
import com.denisrebrof.springboottest.fight.gateways.model.FightNotFoundResponse
import com.denisrebrof.springboottest.fight.gateways.model.FightPlayerStateResponse.Companion.toResponseData
import com.denisrebrof.springboottest.fight.gateways.model.OpponentNotFoundResponse
import com.denisrebrof.springboottest.user.gateways.WSUserEmptyRequestHandler
import com.denisrebrof.springboottest.user.gateways.model.UserNotFoundResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetFightStateRequestHandler @Autowired constructor(
    private val getCurrentFightUseCase: GetCurrentFightUseCase
) : WSUserEmptyRequestHandler(WSCommand.GetFightState.id) {

    override fun handleMessage(userId: Long): ResponseState {
        val currentFight = getCurrentFightUseCase
            .get(userId)
            ?: return FightNotFoundResponse

        return sendFightStateResponse(currentFight, userId)
    }

    private fun sendFightStateResponse(game: FightGame, userId: Long): ResponseState = game.run {
        val playerState = playerStates[userId] ?: return@run UserNotFoundResponse
        val opponentState = playerStates
            .filter { (id, _) -> id != userId }
            .values
            .firstOrNull()
            ?: return@run OpponentNotFoundResponse

        val requestData = FightGameStateResponse(
            playerState = playerState.toResponseData(),
            opponentState = opponentState.toResponseData(),
            stateCode = state.id,
        )
        return@run Json
            .encodeToString(requestData)
            .let(ResponseState::CreatedResponse)
    }
}