package com.denisrebrof.springboottest.fight.gateways.model

import com.denisrebrof.springboottest.commands.domain.model.ResponseErrorCodes
import com.denisrebrof.springboottest.commands.domain.model.ResponseState

val FightNotFoundResponse = ResponseState.ErrorResponse(
    ResponseErrorCodes.Internal.code,
    Exception("Fight not found")
)

val InvalidFightStateResponse = ResponseState.ErrorResponse(
    ResponseErrorCodes.Internal.code,
    Exception("Game is in invalid state")
)

val OpponentNotFoundResponse = ResponseState.ErrorResponse(
    ResponseErrorCodes.UserNotFound.code,
    Exception("Opponent not found")
)