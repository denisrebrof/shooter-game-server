package com.denisrebrof.springboottest.lobby.gateways

import com.denisrebrof.springboottest.session.domain.WSSessionRepository
import com.denisrebrof.springboottest.session.domain.WSSessionRepository.SessionState
import com.denisrebrof.springboottest.session.domain.WSSessionRepository.SessionStateUpdate
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClearLobbyHandler @Autowired constructor(
    sessionRepository: WSSessionRepository
) : DisposableService() {

    override val handler = sessionRepository
        .getSessionEventsFlow()
        .filter { event -> event.state == SessionState.Disconnected }
        .map(SessionStateUpdate::userId)
        .onBackpressureBuffer()
        .subscribeDefault()
}