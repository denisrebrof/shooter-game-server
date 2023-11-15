package com.denisrebrof.commands.domain.model

private val trueResponse = ResponseState.CreatedResponse("true")
private val falseResponse = ResponseState.CreatedResponse("false")
private val zeroResponse = ResponseState.CreatedResponse("0")

val ResponseState.Companion.True: ResponseState
    get() = trueResponse

val ResponseState.Companion.False: ResponseState
    get() = falseResponse

val ResponseState.Companion.Zero: ResponseState
    get() = zeroResponse

fun ResponseState.Companion.fromBoolean(value: Boolean) = when {
    value -> trueResponse
    else -> falseResponse
}

fun ResponseState.Companion.fromLong(value: Long) = value.toString().let(ResponseState::CreatedResponse)
fun ResponseState.Companion.fromInt(value: Int) = value.toString().let(ResponseState::CreatedResponse)
