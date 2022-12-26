package com.denisrebrof.springboottest.commands.gateways

import com.denisrebrof.springboottest.session.domain.WSSessionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import kotlin.reflect.safeCast

@Service
class WSNotificationService @Autowired constructor(
    private val sessionRepository: WSSessionRepository
) {
    fun send(
        userId: Long,
        commandId: Long,
        data: String,
        responseId: String = ""
    ) {
        val responseSession = sessionRepository
            .getSession(userId)
            .let(WSSessionRepository.SessionState.SessionExists::class::safeCast)
            ?.session
            ?: return
        val responseText = "$commandId$delimiter$responseId$delimiter$data"
        TextMessage(responseText).let(responseSession::sendMessage)
    }

    companion object {
        private const val delimiter = '$'
    }
}