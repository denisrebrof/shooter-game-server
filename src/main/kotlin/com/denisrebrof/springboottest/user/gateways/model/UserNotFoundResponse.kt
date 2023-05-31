package com.denisrebrof.springboottest.user.gateways.model

import com.denisrebrof.springboottest.commands.domain.model.ResponseErrorCodes
import com.denisrebrof.springboottest.commands.domain.model.ResponseState

val UserNotFoundResponse = ResponseState.ErrorResponse(
    ResponseErrorCodes.UserNotFound.code,
    Exception("User not found")
)