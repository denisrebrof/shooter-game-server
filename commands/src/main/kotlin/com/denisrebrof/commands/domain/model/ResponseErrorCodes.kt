package com.denisrebrof.commands.domain.model

enum class ResponseErrorCodes(val code: Long) {
    Internal(0L),
    Unauthorized(1L),
    UserNotFound(2L),
    InvalidRequest(3L),
}