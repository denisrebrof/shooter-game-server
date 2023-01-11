package com.denisrebrof.springboottest.user.domain.repositories

import com.denisrebrof.springboottest.user.domain.model.User
import com.denisrebrof.springboottest.user.domain.model.UserIdentity
import kotlin.reflect.safeCast

interface ICreateUserRepository {
    fun createUser(identity: UserIdentity): CreateUserResult

    sealed class CreateUserResult(val success: Boolean) {
        data class Success(val user: User) : CreateUserResult(true)
        data class Failure(val reason: Reason) : CreateUserResult(false) {
            enum class Reason {
                IncorrectIdentity,
                IdentityCollision,
                Unknown
            }
        }

        fun getUserOrNull(): User? = Success::class.safeCast(this)?.user
    }
}