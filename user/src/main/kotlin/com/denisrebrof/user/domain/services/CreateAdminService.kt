package com.denisrebrof.user.domain.services

import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.model.UserIdentityType
import com.denisrebrof.user.domain.repositories.ICreateUserRepository
import com.denisrebrof.user.domain.repositories.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CreateAdminService @Autowired constructor(
    private val repository: IUserRepository,
    private val createUserRepository: ICreateUserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    init {
        createTestUser()
    }

    private fun createTestUser() {
        val identity = UserIdentity("drebrov", UserIdentityType.Username, passwordEncoder.encode("ekabz0ks"))
        if (repository.find(identity) != null)
            return

        createUserRepository.createUser(identity)
    }
}