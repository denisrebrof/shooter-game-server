package com.denisrebrof.springboottest.balance.domain

import com.denisrebrof.springboottest.balance.domain.repositories.IBalanceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class IncreaseBalanceUseCase @Autowired constructor(
    private val notifyBalanceUpdateUseCase: NotifyBalanceUpdateUseCase,
    private val balanceRepository: IBalanceRepository
) {
    fun increase(userId: Long, amount: Long, currencyType: String) {
        val newAmount = balanceRepository.get(userId, currencyType) + amount
        balanceRepository.set(userId, currencyType, newAmount)
        notifyBalanceUpdateUseCase.notify(userId)
    }
}