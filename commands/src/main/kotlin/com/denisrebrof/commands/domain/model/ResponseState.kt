package com.denisrebrof.commands.domain.model

sealed class ResponseState {
    object NoResponse : ResponseState()
    data class ErrorResponse(val code: Long, val exception: Exception) : ResponseState()
    data class CreatedResponse(val response: String) : ResponseState()

    companion object
}

val UserNotFoundDefaultResponse = ResponseState.ErrorResponse(
    ResponseErrorCodes.UserNotFound.code,
    Exception("User not found!")
)

val InternalErrorDefaultResponse = ResponseState.ErrorResponse(
    ResponseErrorCodes.Internal.code,
    Exception("Internal server error")
)