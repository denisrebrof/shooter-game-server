package com.denisrebrof.springboottest.balance.data

import com.denisrebrof.springboottest.balance.domain.repositories.IUserCurrencyRepository

object EnumerationUserCurrencyRepository : IUserCurrencyRepository {
    override fun getCurrencies(userId: Long): Set<String> = CurrencyType
        .values()
        .map(CurrencyType::id)
        .toSet()
}