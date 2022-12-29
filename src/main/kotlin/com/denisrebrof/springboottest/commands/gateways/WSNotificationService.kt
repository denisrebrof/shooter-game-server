package com.denisrebrof.springboottest.commands.gateways

import com.denisrebrof.springboottest.commands.domain.model.Notification
import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.session.data.WSConnectedSessionRepository
import com.denisrebrof.springboottest.session.domain.IWSConnectedSessionRepository.SessionState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import kotlin.reflect.safeCast

@Service
class WSNotificationService @Autowired constructor(
    private val sessionRepository: WSConnectedSessionRepository
) {
    fun send(notification: Notification) {
        val responseSession = getSessionNullable(notification.sessionId) ?: return
        val notificationContent = when (val data = notification.content) {
            is NotificationContent.Data -> data.text
            is NotificationContent.Error -> data.getResponseText()
        }
        val responseText = StringBuilder()
            .append(notification.commandId)
            .append(delimiter)
            .append(notification.responseId)
            .append(delimiter)
            .append(notificationContent)
            .toString()
        TextMessage(responseText).let(responseSession::sendMessage)
    }

    private fun getSessionNullable(sessionId: String) = sessionRepository
        .getSession(sessionId)
        .let(SessionState.Exists::class::safeCast)
        ?.session

    private fun NotificationContent.Error.getResponseText(): String = StringBuilder()
        .append(errorPrefix)
        .append(this.errorCode)
        .append(errorPrefix)
        .append(this.exception)
        .toString()

    companion object {
        private const val delimiter = '$'
        private const val errorPrefix = '#'
    }
}