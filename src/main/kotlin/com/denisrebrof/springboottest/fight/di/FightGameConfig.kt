package com.denisrebrof.springboottest.fight.di

import com.denisrebrof.springboottest.fight.domain.FightMatchSyncMatchRepositoryDecorator
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class FightGameConfig {
    @Bean
    @Primary
    fun getDecoratedMatchRepository(
        source: FightMatchSyncMatchRepositoryDecorator
    ): IMatchRepository = source
}