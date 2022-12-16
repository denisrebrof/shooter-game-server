package com.denisrebrof.springboottest.messaging

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import kotlin.reflect.safeCast

@Component
class MessagingWebSocketEventListener {

    @Autowired
    private lateinit var sendingOperations: SimpMessageSendingOperations

    private val logger = LoggerFactory.getLogger(MessagingWebSocketEventListener::class.java)

    @EventListener
    fun onConnected(event: SessionConnectedEvent) {
        logger.info("New connection created!")
    }

    @EventListener
    fun onDisconnected(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val username = headerAccessor
            .sessionAttributes
            ?.get("username")
            ?.let(String::class::safeCast)
            ?: "undefined"

        val message = ChatMessage(MessageType.DISCONNECT, username)
        sendingOperations.convertAndSend("/topic/public", message)
        logger.info("Disconnect!")
    }
}