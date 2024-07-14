package com.denisrebrof.matches.gateways

import com.denisrebrof.commands.domain.model.*
import com.denisrebrof.matches.domain.services.MatchService
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LeaveMatchRequestHandler @Autowired constructor(
    private val matchService: MatchService
) : WSUserEmptyRequestHandler(WSCommand.LeaveMatch.id) {
    override fun handleMessage(userId: Long): ResponseState {
        val matchId = matchService
            .getMatchIdByUserId(userId)
            ?: return ResponseState.False

        matchService.removeUser(matchId, userId)
        return ResponseState.True
    }
}