package com.denisrebrof.balance.domain

import com.denisrebrof.balance.domain.model.CurrencyType
import com.denisrebrof.balance.domain.repositories.IBalanceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DecreaseBalanceUseCase @Autowired constructor(
    private val notifyBalanceUpdateUseCase: NotifyBalanceUpdateUseCase,
    private val balanceStateRepository: IBalanceRepository
) {
    fun decrease(userId: Long, amount: Long, currencyType: CurrencyType): Boolean {
        val newAmount = balanceStateRepository.get(userId, currencyType.id) - amount
        if (newAmount < 0)
            return false

        balanceStateRepository.set(userId, currencyType.id, newAmount)
        notifyBalanceUpdateUseCase.notify(userId)
        return true
    }
}