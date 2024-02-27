package com.denisrebrof.commands.gateways

import com.denisrebrof.commands.domain.WSConnectedSessionRepository
import com.denisrebrof.commands.domain.IWSConnectedSessionRepository.SessionState
import com.denisrebrof.commands.domain.model.Notification
import com.denisrebrof.commands.domain.model.NotificationContent
import com.denisrebrof.utils.subscribeWithLogError
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Schedules
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import java.util.concurrent.Executors
import kotlin.reflect.safeCast

@Service
class WSNotificationService @Autowired constructor(
    private val sessionRepository: WSConnectedSessionRepository
) {

    private val notificationsScheduler = Executors
        .newFixedThreadPool(10)
        .let(Schedulers::from)

    fun send(notification: Notification) {
        val responseSession = getSessionNullable(notification.sessionId) ?: return
        val notificationContent = when (val data = notification.content) {
            is NotificationContent.Data -> data.text
            is NotificationContent.Error -> data.getResponseText()
        }
        val responseText = StringBuilder()
            .append(notification.commandId)
            .append(DELIMITER)
            .append(notification.responseId)
            .append(DELIMITER)
            .append(notificationContent)
            .toString()

        val message = TextMessage(responseText)
        Single
            .fromCallable {
                synchronized(responseSession) { responseSession.sendMessage(message) }
            }
            .subscribeOn(notificationsScheduler)
            .subscribeWithLogError()
    }

    private fun getSessionNullable(sessionId: String) = sessionRepository
        .getSession(sessionId)
        .let(SessionState.Exists::class::safeCast)
        ?.session

    private fun NotificationContent.Error.getResponseText(): String = StringBuilder()
        .append(ERROR_PREFIX)
        .append(this.errorCode)
        .append(ERROR_PREFIX)
        .append(this.exception)
        .toString()

    companion object {
        private const val DELIMITER = '$'
        private const val ERROR_PREFIX = '#'
    }
}