package com.denisrebrof.userdata.repositories

import com.denisrebrof.balance.domain.repositories.IBalanceRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class BalanceRepositoryImplTest @Autowired constructor (
    private val balanceRepository: IBalanceRepository
) {

    @Test
    fun get() {

    }

    @Test
    fun set() {

    }
}