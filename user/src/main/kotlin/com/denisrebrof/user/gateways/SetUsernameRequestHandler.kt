package com.denisrebrof.user.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.simplestats.domain.ISimpleStatsReceiver
import com.denisrebrof.user.domain.SetUsernameUseCase
import com.denisrebrof.user.domain.model.SetUsernameResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SetUsernameRequestHandler @Autowired constructor(
    private val setUsernameUseCase: SetUsernameUseCase,
    private val statReceiver: ISimpleStatsReceiver
) : WSUserRequestHandler<String>(WSCommand.SetUserName.id) {

    override fun parseData(data: String): String = data

    override fun handleMessage(userId: Long, data: String): ResponseState {
        val result = setUsernameUseCase.setUsername(userId, data)
        statReceiver.addLog("Update username to $data with result $result")
        return result.response
    }

    private val SetUsernameResult.response
        get() = code.toString().let(ResponseState::CreatedResponse)
}