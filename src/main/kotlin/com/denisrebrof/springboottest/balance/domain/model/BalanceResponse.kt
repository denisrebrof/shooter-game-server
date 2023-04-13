package com.denisrebrof.springboottest.balance.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    val currencies: List<String>,
    val amounts: List<Long>,
) {
    companion object {
        fun fromCurrencies(currencies: Map<String, Long>) = BalanceResponse(
            currencies = currencies.keys.toList(),
            amounts = currencies.values.toList()
        )
    }
}