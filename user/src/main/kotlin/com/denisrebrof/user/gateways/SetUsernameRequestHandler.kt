package com.denisrebrof.user.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.user.domain.SetUsernameUseCase
import com.denisrebrof.user.domain.model.SetUsernameResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SetUsernameRequestHandler @Autowired constructor(
    private val setUsernameUseCase: SetUsernameUseCase,
) : WSUserRequestHandler<String>(WSCommand.SetUserName.id) {

    override fun parseData(data: String): String = data

    override fun handleMessage(userId: Long, data: String): ResponseState = setUsernameUseCase
        .setUsername(userId, data)
        .response

    private val SetUsernameResult.response
        get() = code.toString().let(ResponseState::CreatedResponse)
}