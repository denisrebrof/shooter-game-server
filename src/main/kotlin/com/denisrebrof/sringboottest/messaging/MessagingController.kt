package com.denisrebrof.sringboottest.messaging

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller

@Controller
class MessagingController {

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    fun sendMessage(@Payload chatMessage: ChatMessage): ChatMessage = chatMessage

    @MessageMapping("/chat.newUser")
    @SendTo("/topic/public")
    fun createNewUser(
        @Payload chatMessage: ChatMessage,
        headerAccessor: SimpMessageHeaderAccessor
    ): ChatMessage {
        headerAccessor.sessionAttributes?.put("username", chatMessage.sender)
        return chatMessage
    }
}