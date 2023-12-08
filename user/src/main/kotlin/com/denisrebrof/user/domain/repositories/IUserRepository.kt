package com.denisrebrof.user.domain.repositories

import com.denisrebrof.user.domain.model.User
import com.denisrebrof.user.domain.model.UserIdentity

interface IUserRepository {
    fun find(identity: UserIdentity): User?
    fun updateNick(identity: UserIdentity, newNick: String): Boolean
}