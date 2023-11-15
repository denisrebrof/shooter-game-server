package com.denisrebrof.matches.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.fromBoolean
import com.denisrebrof.matches.domain.services.MatchService
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MatchStateRequestHandler @Autowired constructor(
    private val matchService: MatchService
) : WSUserEmptyRequestHandler(WSCommand.GetMatch.id) {

    override fun handleMessage(userId: Long): ResponseState = matchService
        .hasByUserId(userId)
        .let(ResponseState.Companion::fromBoolean)
}