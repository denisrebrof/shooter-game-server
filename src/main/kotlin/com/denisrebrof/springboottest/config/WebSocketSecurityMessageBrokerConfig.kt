package com.denisrebrof.springboottest.config

import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer


class WebSocketSecurityMessageBrokerConfig : AbstractSecurityWebSocketMessageBrokerConfigurer() {

    override fun configureInbound(messages: MessageSecurityMetadataSourceRegistry) {
        messages
            .simpDestMatchers("/secured/**")
            .authenticated()
            .anyMessage()
            .authenticated()
    }
}