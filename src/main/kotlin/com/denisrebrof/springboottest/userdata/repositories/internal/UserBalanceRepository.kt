package com.denisrebrof.springboottest.userdata.repositories.internal

import com.denisrebrof.springboottest.userdata.model.UserBalance
import org.springframework.data.jpa.repository.JpaRepository

interface UserBalanceRepository : JpaRepository<UserBalance, Long> {
    fun findByUserIdAndCurrencyId(userId: Long, currencyId: String): UserBalance?
    fun findByUserId(userId: Long): List<UserBalance>
}