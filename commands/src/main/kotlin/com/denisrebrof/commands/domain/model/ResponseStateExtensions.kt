package com.denisrebrof.commands.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

inline fun <reified T: Any> T.toResponse(): ResponseState.CreatedResponse = Json
    .encodeToString(this)
    .let(ResponseState::CreatedResponse)
