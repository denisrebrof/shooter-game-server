package com.denisrebrof.commands.domain.model

import java.lang.Exception

data class Notification(
    val sessionId: String,
    val commandId: Long,
    val content: NotificationContent,
    val responseId: String = ""
)

sealed class NotificationContent {
    data class Error(val errorCode: Long, val exception: Exception): NotificationContent()
    data class Data(val text: String): NotificationContent()

    companion object
}