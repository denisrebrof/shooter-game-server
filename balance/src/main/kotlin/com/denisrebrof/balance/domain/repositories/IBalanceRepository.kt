package com.denisrebrof.balance.domain.repositories

interface IBalanceRepository {
    fun get(userId: Long, currencyId: String): Long
    fun set(userId: Long, currencyId: String, amount: Long)
}