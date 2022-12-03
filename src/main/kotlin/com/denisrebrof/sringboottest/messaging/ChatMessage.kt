package com.denisrebrof.sringboottest.messaging

import java.util.*


data class ChatMessage(
    val messageType: MessageType,
    val sender: String,
    val content: String = "",
    val time: String = Date().time.toString(),
)
