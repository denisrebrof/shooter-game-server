package com.denisrebrof.springboottest.balance.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    val currencies: Map<String, Long>
)