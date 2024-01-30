package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.toResponse
import com.denisrebrof.matches.domain.model.Match
import com.denisrebrof.matches.domain.services.MatchService
import com.denisrebrof.matches.domain.services.MatchService.Companion.MAX_PARTICIPANTS
import com.denisrebrof.shooter.domain.model.responseType
import com.denisrebrof.shooter.domain.services.ShooterGameService
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterGamesListRequestHandler @Autowired constructor(
    private val matchService: MatchService,
    private val gameService: ShooterGameService
) : WSUserEmptyRequestHandler(WSCommand.GetGames.id) {

    override fun handleMessage(userId: Long): ResponseState = matchService
        .getMatches()
        .mapNotNull(::getResponseItem)
        .let(::ShooterGamesListResponse)
        .toResponse()

    private fun getResponseItem(
        match: Match
    ): ShooterGamesListItemResponseData? {
        val game = gameService.get(match.id) ?: return null
        return ShooterGamesListItemResponseData(
            matchId = match.id,
            currentParticipants = match.participants.size,
            maxParticipants = MAX_PARTICIPANTS,
            stateCode = game.state.responseType.code,
        )
    }

    @Serializable
    data class ShooterGamesListResponse(
        val matches: List<ShooterGamesListItemResponseData>
    )

    @Serializable
    data class ShooterGamesListItemResponseData(
        val matchId: String,
        val currentParticipants: Int,
        val maxParticipants: Int,
        val stateCode: Long
    )
}