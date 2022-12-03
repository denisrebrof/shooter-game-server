package com.denisrebrof.sringboottest.messaging.components

import com.denisrebrof.sringboottest.messaging.ChatMessage
import com.denisrebrof.sringboottest.messaging.MessageType
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class ChatMessageComponent {
    @Bean
    fun getChatMessageBean(): ChatMessage = ChatMessage(
        messageType = MessageType.CONNECT,
        content = "BEan connect",
        sender = "2",
        time = "Bean Time"
    )
}