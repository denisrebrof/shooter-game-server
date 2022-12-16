package com.denisrebrof.springboottest.messaging

import java.util.*


data class ChatMessage(
    val messageType: MessageType,
    val sender: String,
    val content: String = "",
    val receiverId: String = "",
    val time: String = Date().time.toString(),
)
