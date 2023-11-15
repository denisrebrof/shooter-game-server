package com.denisrebrof.balance.gateways

import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import com.denisrebrof.balance.domain.UserBalancesUseCase
import com.denisrebrof.balance.domain.model.BalanceResponse
import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BalanceStateRequestHandler @Autowired constructor(
    private val userBalancesUseCase: UserBalancesUseCase
) : WSUserEmptyRequestHandler(WSCommand.BalanceState.id) {

    override fun handleMessage(userId: Long): ResponseState = userBalancesUseCase
        .getCurrencyBalances(userId)
        .let(BalanceResponse.Companion::fromCurrencies)
        .let(Json::encodeToString)
        .let(ResponseState::CreatedResponse)
}