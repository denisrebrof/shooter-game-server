package com.denisrebrof.springboottest

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import org.springframework.stereotype.Service

@Service
class PingCommandRequestHandler : WSUserEmptyRequestHandler(WSCommand.Ping.id) {
    override fun handleMessage(userId: Long): ResponseState = System
        .currentTimeMillis()
        .toString()
        .let(ResponseState::CreatedResponse)
}