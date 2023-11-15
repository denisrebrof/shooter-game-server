package com.denisrebrof.user.domain

import com.denisrebrof.commands.domain.model.Notification
import com.denisrebrof.commands.domain.model.NotificationContent
import com.denisrebrof.user.domain.repositories.IWSUserSessionRepository
import com.denisrebrof.user.domain.repositories.IWSUserSessionRepository.UserSessionState
import com.denisrebrof.commands.gateways.WSNotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.reflect.safeCast

@Service
class SendUserNotificationUseCase @Autowired constructor(
    private val notificationService: WSNotificationService,
    private val userSessionRepository: IWSUserSessionRepository
) {
    fun send(
        userIds: List<Long>,
        commandId: Long,
        content: NotificationContent
    ) = userIds.forEach { userId ->
        send(userId, commandId, content)
    }

    fun send(
        userId: Long,
        commandId: Long,
        content: NotificationContent
    ) {
        val sessionId = userSessionRepository
            .getUserSession(userId)
            .let(UserSessionState.Exists::class::safeCast)
            ?.session
            ?.id
            ?: return

        Notification(sessionId, commandId, content).let(notificationService::send)
    }
}