package com.denisrebrof.gameresources.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.fromInt
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import org.springframework.stereotype.Service

@Service
class ResourcesVersionRequestHandler : WSUserEmptyRequestHandler(WSCommand.ResVersion.id) {
    override fun handleMessage(userId: Long): ResponseState = ResponseState.fromInt(3)
}