package com.denisrebrof.springboottest.matches.di

import com.denisrebrof.springboottest.matches.data.MatchRepository
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MatchConfig {
    @Bean
    @Qualifier("Base")
    fun getBaseMatchesRepository(source: MatchRepository): IMatchRepository = source
}