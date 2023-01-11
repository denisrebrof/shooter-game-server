package com.denisrebrof.springboottest.balance.domain

import com.denisrebrof.springboottest.balance.domain.repositories.IBalanceRepository
import org.springframework.beans.factory.annotation.Autowired

class DecreaseBalanceUseCase @Autowired constructor(
    private val notifyBalanceUpdateUseCase: NotifyBalanceUpdateUseCase,
    private val balanceStateRepository: IBalanceRepository
) {
    fun decrease(userId: Long, amount: Long, currencyType: String): Boolean {
        val newAmount = balanceStateRepository.get(userId, currencyType) - amount
        if (newAmount < 0)
            return false

        balanceStateRepository.set(userId, currencyType, newAmount)
        notifyBalanceUpdateUseCase.notify(userId)
        return true
    }
}