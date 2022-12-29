package com.denisrebrof.springboottest.lobby.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.lobby.domain.LobbyRepository
import com.denisrebrof.springboottest.lobby.domain.model.LobbyUserState
import com.denisrebrof.springboottest.user.gateways.WSUserEmptyRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LobbyStateRequestHandler @Autowired constructor(
    private val lobbyRepository: LobbyRepository
) : WSUserEmptyRequestHandler(WSCommand.LobbyState.id) {
    override fun handleMessage(userId: Long): ResponseState {
        val inLobby = lobbyRepository.isInLobby(userId)
        val lobbyState = if (inLobby) LobbyUserState.Joined else LobbyUserState.NotIn
        return lobbyState.code.toString().let(ResponseState::CreatedResponse)
    }
}