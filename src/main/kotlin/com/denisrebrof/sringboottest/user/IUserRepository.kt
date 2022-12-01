package com.denisrebrof.sringboottest.user

import com.denisrebrof.sringboottest.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface IUserRepository : JpaRepository<User, String> {
    fun findUserByUsername(username: String): List<User>
    fun countUsersByUsername(username: String): Long
}