package com.denisrebrof.springboottest.commands.gateways

import com.denisrebrof.springboottest.commands.domain.RequestsRepository
import com.denisrebrof.springboottest.commands.domain.model.RequestData
import com.denisrebrof.springboottest.commands.gateways.WSRequestHandler.HandleRawMessageResult
import com.denisrebrof.springboottest.user.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import kotlin.reflect.safeCast

@Service
class WSRequestsRouter @Autowired constructor(
    private val handlers: List<WSRequestHandler<*>>,
    private val requestsRepository: RequestsRepository
) {

    fun sendRequest(
        userId: Long,
        message: WebSocketMessage<*>
    ): MessageRoutingResult {
        val textPayload = message.textPayload ?: return MessageRoutingResult.InvalidMessage

        val payloadSegments = textPayload.split(commandDelimiter)
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
        val handleResult = handler.handleRawMessage(userId, contentText)
        if (handleResult is HandleRawMessageResult.InvalidData)
            return MessageRoutingResult.DataParsingError

        val responseId = payloadSegments
            .getOrNull(2)
            ?: return MessageRoutingResult.DataParsingError

        val responseState = handleResult
            .let(HandleRawMessageResult.Success::class::safeCast)
            ?.response
            ?: return MessageRoutingResult.Delivered

        val responseData = responseState
            .let(WSRequestHandler.ResponseState.CreatedResponse::class::safeCast)
            ?.response
            ?: return MessageRoutingResult.Delivered

        RequestData(userId, commandIdCode, responseData, responseId).let(requestsRepository::add)
        return MessageRoutingResult.Delivered
    }

    private val WebSocketMessage<*>.textPayload: String?
        get() = TextMessage::class.safeCast(this)?.payload

    companion object {
        private const val commandDelimiter = '$'
    }

    enum class MessageRoutingResult {
        Delivered,
        InvalidMessage,
        HandlerNotFound,
        DataParsingError
    }

}