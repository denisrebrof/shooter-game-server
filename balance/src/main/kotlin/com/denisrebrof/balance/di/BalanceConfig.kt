package com.denisrebrof.balance.di

import com.denisrebrof.balance.data.EnumerationUserCurrencyRepository
import com.denisrebrof.balance.domain.repositories.IUserCurrencyRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BalanceConfig {
    @Bean
    fun getUserCurrencyRepository(): IUserCurrencyRepository = EnumerationUserCurrencyRepository
}