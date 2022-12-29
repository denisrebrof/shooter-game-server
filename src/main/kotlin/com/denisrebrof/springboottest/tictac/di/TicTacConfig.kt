package com.denisrebrof.springboottest.tictac.di

import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.tictac.domain.TicTacGameMatchSyncMatchRepositoryDecorator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TicTacConfig {
    @Bean
    @Primary
    fun getDecoratedMatchRepository(
        source: TicTacGameMatchSyncMatchRepositoryDecorator
    ): IMatchRepository = source
}