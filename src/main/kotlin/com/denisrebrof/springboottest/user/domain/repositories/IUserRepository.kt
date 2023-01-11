package com.denisrebrof.springboottest.user.domain.repositories

import com.denisrebrof.springboottest.user.domain.model.User
import com.denisrebrof.springboottest.user.domain.model.UserIdentity

interface IUserRepository {
    fun find(identity: UserIdentity): User?
}