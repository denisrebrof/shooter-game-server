package com.denisrebrof.springboottest.balance.di

import com.denisrebrof.springboottest.balance.data.EnumerationUserCurrencyRepository
import com.denisrebrof.springboottest.balance.domain.repositories.IUserCurrencyRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BalanceConfig {
    @Bean
    fun getUserCurrencyRepository(): IUserCurrencyRepository = EnumerationUserCurrencyRepository
}