package com.denisrebrof.springboottest.commands.domain.model

enum class ResponseErrorCodes(val code: Long) {
    Internal(0L),
    Unauthorized(1L),
    UserNotFound(2L),
}