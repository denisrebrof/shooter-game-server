package com.denisrebrof.springboottest.session.gateways

import com.denisrebrof.springboottest.commands.gateways.WSRequestsRouter
import com.denisrebrof.springboottest.session.domain.HandleUserConnectionUseCase
import com.denisrebrof.springboottest.session.domain.UserBySessionUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Service
class WSHandler @Autowired constructor(
    private val handleUserConnectionUseCase: HandleUserConnectionUseCase,
    private val userBySessionUseCase: UserBySessionUseCase,
    private val notificationsService: WSRequestsRouter
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        handleUserConnectionUseCase.addSession(session)
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        val userId = userBySessionUseCase
            .getUserNullable(session)
            ?.id
            ?: return session.close(CloseStatus.SERVER_ERROR)

        notificationsService.sendRequest(userId, message)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        handleUserConnectionUseCase.removeSession(session)
    }
}