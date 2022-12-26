package com.denisrebrof.springboottest.matches.gateways

import com.denisrebrof.springboottest.commands.domain.model.WSCommandId
import com.denisrebrof.springboottest.commands.gateways.WSEmptyRequestHandler
import com.denisrebrof.springboottest.matches.domain.MatchRepository
import com.denisrebrof.springboottest.matches.domain.model.Match
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetActiveMatchRequestHandler @Autowired constructor(
    private val matchRepository: MatchRepository
) : WSEmptyRequestHandler(WSCommandId.GetMatch.id) {
    private val emptyMatch = Match(
        id = "",
        createdTime = 0L,
        participantIds = listOf()
    )

    private val defaultResponse = Json
        .encodeToString(emptyMatch)
        .let(ResponseState::CreatedResponse)

    override fun handleMessage(userId: Long): ResponseState {
        val match = matchRepository
            .getMatchIdByUserId(userId)
            ?.let(matchRepository::get)
            ?: return defaultResponse
        return Json
            .encodeToString(match)
            .let(ResponseState::CreatedResponse)
    }
}