package com.denisrebrof.commands.gateways

import com.denisrebrof.commands.domain.model.Notification
import com.denisrebrof.commands.domain.model.NotificationContent
import com.denisrebrof.commands.domain.model.NotificationContent.Data
import com.denisrebrof.commands.domain.model.NotificationContent.Error
import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.gateways.WSSessionRequestHandler.HandleRawMessageResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import kotlin.reflect.safeCast

@Service
class WSRequestsRouter @Autowired constructor(
    private val handlers: List<WSSessionRequestHandler<*>>,
    private val notificationService: WSNotificationService
) {

    fun sendRequest(
        sessionId: String,
        message: WebSocketMessage<*>
    ): MessageRoutingResult {
        val textPayload = message.textPayload ?: return MessageRoutingResult.InvalidMessage

        val payloadSegments = textPayload.split(COMMAND_DELIMITER)
        if (payloadSegments.size < 2)
            return MessageRoutingResult.InvalidMessage

        val commandIdCode = payloadSegments
            .first()
            .toLongOrNull()
            ?: return MessageRoutingResult.InvalidMessage

        val handler = handlers
            .firstOrNull { it.id == commandIdCode }
            ?: return MessageRoutingResult.HandlerNotFound

        val contentText = payloadSegments[1]
        val handleResult = handler.handleRawMessage(sessionId, contentText)
        if (handleResult is HandleRawMessageResult.InvalidData)
            return MessageRoutingResult.DataParsingError

        val responseId = payloadSegments
            .getOrNull(2)
            ?: return MessageRoutingResult.DataParsingError

        val responseState = handleResult
            .let(HandleRawMessageResult.Success::class::safeCast)
            ?.response
            ?: return MessageRoutingResult.Delivered

        val responseData = getNotificationContent(responseState)
            ?: return MessageRoutingResult.Delivered

        Notification(
            sessionId = sessionId,
            commandId = commandIdCode,
            content = responseData,
            responseId = responseId
        ).let(notificationService::send)
        return MessageRoutingResult.Delivered
    }

    private fun getNotificationContent(responseState: ResponseState): NotificationContent? = when (responseState) {
        is ResponseState.CreatedResponse -> Data(responseState.response)
        is ResponseState.ErrorResponse -> Error(responseState.code, responseState.exception)
        ResponseState.NoResponse -> null
    }

    private val WebSocketMessage<*>.textPayload: String?
        get() = TextMessage::class.safeCast(this)?.payload

    companion object {
        private const val COMMAND_DELIMITER = '$'
    }

    enum class MessageRoutingResult {
        Delivered,
        InvalidMessage,
        HandlerNotFound,
        DataParsingError
    }

}