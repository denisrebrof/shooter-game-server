package com.denisrebrof.springboottest.session.gateways

import DisposableService
import com.denisrebrof.springboottest.commands.domain.NotificationsRepository
import com.denisrebrof.springboottest.commands.gateways.WSNotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import subscribeDefault

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