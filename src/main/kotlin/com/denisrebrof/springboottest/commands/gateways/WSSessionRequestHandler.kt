package com.denisrebrof.springboottest.commands.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState

abstract class WSSessionRequestHandler<MESSAGE_DATA : Any>(open val id: Long) {

    @Throws
    protected abstract fun parseData(data: String): MESSAGE_DATA

    protected abstract fun handleMessage(sessionId: String, data: MESSAGE_DATA): ResponseState

    internal fun handleRawMessage(
        sessionId: String,
        data: String
    ): HandleRawMessageResult {
        val messageData = kotlin
            .runCatching { parseData(data) }
            .onFailure(::println)
            .getOrNull()
            ?: return HandleRawMessageResult.InvalidData

        return handleMessage(sessionId, messageData).let(HandleRawMessageResult::Success)
    }

    internal sealed class HandleRawMessageResult {
        object InvalidData : HandleRawMessageResult()
        data class Success(val response: ResponseState) : HandleRawMessageResult()
    }
}