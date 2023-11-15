package com.denisrebrof.userdata.internal

import com.denisrebrof.userdata.model.UserBalance
import org.springframework.data.jpa.repository.JpaRepository

interface UserBalanceRepository : JpaRepository<UserBalance, Long> {
    fun findByUserIdAndCurrencyId(userId: Long, currencyId: String): UserBalance?
    fun findByUserId(userId: Long): List<UserBalance>
}