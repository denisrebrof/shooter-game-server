package com.denisrebrof.springboottest.userdata.repositories

import com.denisrebrof.springboottest.balance.domain.repositories.IBalanceRepository
import com.denisrebrof.springboottest.userdata.model.UserBalance
import com.denisrebrof.springboottest.userdata.repositories.internal.UserBalanceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BalanceRepositoryImpl @Autowired constructor(
    private val balanceRepository: UserBalanceRepository
) : IBalanceRepository {
    override fun get(userId: Long, currencyId: String): Long = balanceRepository
        .findByUserIdAndCurrencyId(userId, currencyId)
        ?.amount
        ?: 0L

    override fun set(userId: Long, currencyId: String, amount: Long) {
        val balance = balanceRepository
            .findByUserIdAndCurrencyId(userId, currencyId)
            ?: UserBalance(userId = userId, currencyId = currencyId)

        balance.copy(amount = amount).let(balanceRepository::save)
    }

}