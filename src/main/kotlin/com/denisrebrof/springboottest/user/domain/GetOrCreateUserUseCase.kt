package com.denisrebrof.springboottest.user.domain

import com.denisrebrof.springboottest.user.domain.model.User
import com.denisrebrof.springboottest.user.domain.model.UserIdentity
import com.denisrebrof.springboottest.user.domain.repositories.ICreateUserRepository
import com.denisrebrof.springboottest.user.domain.repositories.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetOrCreateUserUseCase @Autowired constructor(
    private val userRepository: IUserRepository,
    private val createUserRepository: ICreateUserRepository
) {
    fun getOrCreate(identity: UserIdentity): User? = userRepository
        .find(identity)
        ?: createUser(identity)

    private fun createUser(identity: UserIdentity): User? = createUserRepository
        .createUser(identity)
        .getUserOrNull()
}