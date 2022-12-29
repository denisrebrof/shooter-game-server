package com.denisrebrof.springboottest.commands.domain.model

sealed class ResponseState {
    object NoResponse : ResponseState()
    data class ErrorResponse(val code: Long, val exception: Exception) : ResponseState()
    data class CreatedResponse(val response: String) : ResponseState()
}