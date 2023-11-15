package com.denisrebrof.balance.domain.repositories

interface IUserCurrencyRepository {
    fun getCurrencies(userId: Long): Set<String>
}