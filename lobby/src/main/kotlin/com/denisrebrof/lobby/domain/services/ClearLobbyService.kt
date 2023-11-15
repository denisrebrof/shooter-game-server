package com.denisrebrof.lobby.domain.services

import com.denisrebrof.utils.DisposableService
import com.denisrebrof.lobby.domain.LobbyRepository
import com.denisrebrof.user.domain.repositories.IWSUserSessionEventsRepository
import com.denisrebrof.user.domain.repositories.IWSUserSessionEventsRepository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.denisrebrof.utils.subscribeDefault

@Service
class ClearLobbyService @Autowired constructor(
    sessionEventsRepository: IWSUserSessionEventsRepository,
    lobbyRepository: LobbyRepository
) : DisposableService() {

    override val handler = sessionEventsRepository
        .getSessionEventsFlow()
        .filter { event -> event.type == UserSessionEventType.Disconnected }
        .map(UserSessionEvent::userId)
        .onBackpressureBuffer()
        .subscribeDefault(lobbyRepository::remove)
}