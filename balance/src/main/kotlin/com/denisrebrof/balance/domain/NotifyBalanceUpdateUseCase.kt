package com.denisrebrof.balance.domain

import com.denisrebrof.balance.domain.model.BalanceResponse
import com.denisrebrof.commands.domain.model.NotificationContent.Companion.toNotificationData
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.user.domain.SendUserNotificationUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NotifyBalanceUpdateUseCase @Autowired constructor(
    private val sendUserNotificationUseCase: SendUserNotificationUseCase,
    private val userBalancesUseCase: UserBalancesUseCase
) {
    fun notify(userId: Long) {
        val notification = userBalancesUseCase
            .getCurrencyBalances(userId)
            .let(BalanceResponse.Companion::fromCurrencies)
            .toNotificationData()

        sendUserNotificationUseCase.send(userId, WSCommand.BalanceState.id, notification)
    }
}