package com.denisrebrof.matches.gateways

import com.denisrebrof.commands.domain.model.False
import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.True
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.matches.domain.services.MatchService
import com.denisrebrof.matches.domain.services.MatchService.Companion.MAX_PARTICIPANTS
import com.denisrebrof.user.gateways.WSUserRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class JoinMatchRequestHandler @Autowired constructor(
    private val matchService: MatchService
) : WSUserRequestHandler<String>(WSCommand.JoinMatch.id) {
    override fun parseData(data: String): String = data

    override fun handleMessage(userId: Long, data: String): ResponseState {
        val match = matchService.get(data) ?: return ResponseState.False
        if (match.participants.size >= MAX_PARTICIPANTS)
            return ResponseState.False

        matchService.addUsers(data, userId)
        return ResponseState.True
    }
}