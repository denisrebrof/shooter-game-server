package com.denisrebrof.commands.gateways

import com.denisrebrof.utils.DisposableService
import com.denisrebrof.commands.domain.NotificationsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.denisrebrof.utils.subscribeDefault

@Service
class WSResponsesHandler @Autowired constructor(
    private val notificationService: WSNotificationService,
    notificationsRepository: NotificationsRepository
) : DisposableService() {

    override val handler = notificationsRepository
        .requests
        .onBackpressureBuffer()
        .subscribeDefault(notificationService::send)
}