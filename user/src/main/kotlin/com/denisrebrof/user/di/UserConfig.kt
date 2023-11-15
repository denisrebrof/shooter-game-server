package com.denisrebrof.user.di

import com.denisrebrof.commands.domain.IWSConnectedSessionRepository
import com.denisrebrof.user.domain.RemoveDisconnectedUserMappingConnectedSessionRepositoryDecorator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class UserConfig {
    @Bean
    @Primary
    fun getDecoratedConnectedSessionRepository(
        source: RemoveDisconnectedUserMappingConnectedSessionRepositoryDecorator
    ): IWSConnectedSessionRepository = source
}