package com.denisrebrof.sringboottest.chat

import com.denisrebrof.sringboottest.chat.model.MessageData
import com.denisrebrof.sringboottest.user.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController @Autowired constructor(
    private val messagingTemplate: SimpMessagingTemplate,
    private val userRepository: IUserRepository,
) {
    @MessageMapping("/chat/{recipientId}")
    fun sendMessage(@DestinationVariable recipientId: String, message: MessageData) {
        println("Received message to $recipientId: $message")
        if (!userRepository.existsById(recipientId))
            return

        messagingTemplate.convertAndSend("/topic/messages/$recipientId", message)
    }
}