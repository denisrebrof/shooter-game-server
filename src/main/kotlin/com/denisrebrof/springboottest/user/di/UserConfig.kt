package com.denisrebrof.springboottest.user.di

import com.denisrebrof.springboottest.session.data.WSConnectedSessionRepository
import com.denisrebrof.springboottest.session.domain.IWSConnectedSessionRepository
import com.denisrebrof.springboottest.user.data.WSUserSessionRepository
import com.denisrebrof.springboottest.user.domain.RemoveDisconnectedUserMappingConnectedSessionRepositoryDecorator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class UserConfig {

    @Bean
    @Qualifier("Base")
    fun getBaseConnectedSessionRepository(source: WSConnectedSessionRepository): IWSConnectedSessionRepository = source

    @Bean
    @Primary
    fun getDecoratedConnectedSessionRepository(
        source: RemoveDisconnectedUserMappingConnectedSessionRepositoryDecorator
    ): IWSConnectedSessionRepository = source
}