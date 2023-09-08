package com.denisrebrof.springboottest.ping.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.user.gateways.WSUserEmptyRequestHandler
import org.springframework.stereotype.Service

@Service
class PingCommandRequestHandler : WSUserEmptyRequestHandler(WSCommand.Ping.id) {
    override fun handleMessage(userId: Long): ResponseState = System
        .currentTimeMillis()
        .toString()
        .let(ResponseState::CreatedResponse)
}