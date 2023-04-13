package com.denisrebrof.springboottest.balance.domain

import com.denisrebrof.springboottest.balance.domain.model.BalanceResponse
import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
            .let(Json::encodeToString)
            .let(NotificationContent::Data)

        sendUserNotificationUseCase.send(userId, WSCommand.BalanceState.id, notification)
    }
}