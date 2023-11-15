package com.denisrebrof.commands.di

import com.denisrebrof.commands.domain.IWSConnectedSessionRepository
import com.denisrebrof.commands.domain.WSConnectedSessionRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommandsConfig {
    @Bean
    @Qualifier("Base")
    fun getBaseConnectedSessionRepository(source: WSConnectedSessionRepository): IWSConnectedSessionRepository = source
}