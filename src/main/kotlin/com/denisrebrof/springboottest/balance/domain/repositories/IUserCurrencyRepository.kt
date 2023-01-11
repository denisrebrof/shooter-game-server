package com.denisrebrof.springboottest.balance.domain.repositories

interface IUserCurrencyRepository {
    fun getCurrencies(userId: Long): Set<String>
}