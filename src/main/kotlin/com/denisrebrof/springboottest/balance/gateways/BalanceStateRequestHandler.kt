package com.denisrebrof.springboottest.balance.gateways

import com.denisrebrof.springboottest.balance.domain.UserBalancesUseCase
import com.denisrebrof.springboottest.balance.domain.model.BalanceResponse
import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.user.gateways.WSUserEmptyRequestHandler
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