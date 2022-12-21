package com.denisrebrof.springboottest.messaging

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class MessagingController {

//    @Autowired
//    private lateinit var simpMessagingTemplate: SimpMessagingTemplate
//
//    @MessageMapping("/chat.send.direct")
//    fun sendMessageDirect(
//        @Payload chatMessage: ChatMessage,
//        @Header("simpSessionId") sessionId: String
//    ) {
//        simpMessagingTemplate.convertAndSendToUser(
//            chatMessage.receiverId,
//            "/direct",
//            chatMessage
//        )
//    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    fun sendMessage(@Payload chatMessage: ChatMessage): ChatMessage {
        return chatMessage
    }

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