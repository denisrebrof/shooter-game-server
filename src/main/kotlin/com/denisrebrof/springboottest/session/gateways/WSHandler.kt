package com.denisrebrof.springboottest.session.gateways

import com.denisrebrof.springboottest.commands.gateways.WSRequestsRouter
import com.denisrebrof.springboottest.session.domain.IWSConnectedSessionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Service
class WSHandler @Autowired constructor(
    private val connectedSessionRepository: IWSConnectedSessionRepository,
    private val notificationsService: WSRequestsRouter
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        connectedSessionRepository.addSession(session)
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        notificationsService.sendRequest(session.id, message)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        connectedSessionRepository.removeSession(session.id)
    }
}