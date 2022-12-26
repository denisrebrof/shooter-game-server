package com.denisrebrof.springboottest.session.gateways

import com.denisrebrof.springboottest.commands.domain.RequestsRepository
import com.denisrebrof.springboottest.commands.domain.model.RequestData
import com.denisrebrof.springboottest.commands.gateways.WSNotificationService
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WSResponsesHandler @Autowired constructor(
    private val notificationService: WSNotificationService,
    requestsRepository: RequestsRepository
) : DisposableService() {

    override val handler = requestsRepository
        .requests
        .onBackpressureBuffer()
        .subscribeDefault(::sendResponse)

    private fun sendResponse(data: RequestData) {
        notificationService.send(
            data.userId,
            data.commandId,
            data.data,
            data.responseId
        )
    }
}