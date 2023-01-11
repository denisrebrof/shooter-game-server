package com.denisrebrof.springboottest.balance.domain

import com.denisrebrof.springboottest.balance.domain.repositories.IBalanceRepository
import com.denisrebrof.springboottest.balance.domain.repositories.IUserCurrencyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserBalancesUseCase @Autowired constructor(
    private val userCurrencyRepository: IUserCurrencyRepository,
    private val balanceRepository: IBalanceRepository,
) {
    fun getCurrencyBalances(userId: Long): Map<String, Long> = userCurrencyRepository
        .getCurrencies(userId)
        .associateWith { currencyId ->
            balanceRepository.get(userId, currencyId)
        }
}