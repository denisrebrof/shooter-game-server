package com.denisrebrof.matches.gateways

import com.denisrebrof.matches.domain.usecases.DevLobbyUseCase
import com.denisrebrof.user.gateways.WSUserRequestHandler
import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AddToDevLobbyRequestHandler @Autowired constructor(
    private val devLobbyUseCase: DevLobbyUseCase
) : WSUserRequestHandler<Boolean>(WSCommand.AddToDevLobby.id) {
    override fun parseData(data: String): Boolean = data.toBoolean()

    override fun handleMessage(userId: Long, data: Boolean): ResponseState {
        when {
            data -> devLobbyUseCase.add(userId)
            else -> devLobbyUseCase.remove(userId)
        }
        return ResponseState.CreatedResponse("0")
    }
}