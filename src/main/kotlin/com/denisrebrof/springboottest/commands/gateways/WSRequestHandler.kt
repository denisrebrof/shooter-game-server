package com.denisrebrof.springboottest.commands.gateways

abstract class WSRequestHandler<MESSAGE_DATA : Any>(open val id: Long) {

    @Throws
    protected abstract fun parseData(data: String): MESSAGE_DATA

    protected abstract fun handleMessage(userId: Long, data: MESSAGE_DATA): ResponseState

    internal fun handleRawMessage(
        userId: Long,
        data: String
    ): HandleRawMessageResult {
        val messageData = kotlin
            .runCatching { parseData(data) }
            .onFailure(::println)
            .getOrNull()
            ?: return HandleRawMessageResult.InvalidData

        return handleMessage(userId, messageData).let(HandleRawMessageResult::Success)
    }

    sealed class ResponseState {
        object NoResponse : ResponseState()
        data class CreatedResponse(val response: String) : ResponseState()
    }

    internal sealed class HandleRawMessageResult {
        object InvalidData : HandleRawMessageResult()
        data class Success(val response: ResponseState) : HandleRawMessageResult()
    }
}