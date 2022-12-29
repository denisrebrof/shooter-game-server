package com.denisrebrof.springboottest.lobby.gateways

import com.denisrebrof.springboottest.lobby.domain.LobbyRepository
import com.denisrebrof.springboottest.user.domain.repositories.IWSUserSessionEventsRepository
import com.denisrebrof.springboottest.user.domain.repositories.IWSUserSessionEventsRepository.UserSessionEventType
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClearLobbyHandler @Autowired constructor(
    sessionEventsRepository: IWSUserSessionEventsRepository,
    lobbyRepository: LobbyRepository
) : DisposableService() {

    override val handler = sessionEventsRepository
        .getSessionEventsFlow()
        .filter { event -> event.type == UserSessionEventType.Disconnected }
        .map(IWSUserSessionEventsRepository.UserSessionEvent::userId)
        .onBackpressureBuffer()
        .subscribeDefault(lobbyRepository::remove)
}