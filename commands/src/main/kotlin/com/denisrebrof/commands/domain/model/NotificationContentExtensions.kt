package com.denisrebrof.commands.domain.model

private val trueResponse = NotificationContent.Data("true")
private val falseResponse = NotificationContent.Data("false")
private val zeroResponse = NotificationContent.Data("0")

val NotificationContent.Companion.True: NotificationContent
    get() = trueResponse

val NotificationContent.Companion.False: NotificationContent
    get() = falseResponse

val NotificationContent.Companion.Zero: NotificationContent
    get() = zeroResponse

fun NotificationContent.Companion.fromBoolean(value: Boolean) = when {
    value -> trueResponse
    else -> falseResponse
}