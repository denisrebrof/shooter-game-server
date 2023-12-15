package com.denisrebrof.userdata.internal

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
class UserDataaaRepositoryTest @Autowired constructor (
    private val repository: UserDataRepository
) {
    @Test
    fun testNotNull() {
        assert(repository != null)
    }
}