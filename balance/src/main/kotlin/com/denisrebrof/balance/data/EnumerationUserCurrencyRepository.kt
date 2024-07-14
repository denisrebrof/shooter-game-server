package com.denisrebrof.balance.data

import com.denisrebrof.balance.domain.model.CurrencyType
import com.denisrebrof.balance.domain.repositories.IUserCurrencyRepository

object EnumerationUserCurrencyRepository : IUserCurrencyRepository {
    override fun getCurrencies(userId: Long): Set<String> = CurrencyType
        .values()
        .map(CurrencyType::id)
        .toSet()
}