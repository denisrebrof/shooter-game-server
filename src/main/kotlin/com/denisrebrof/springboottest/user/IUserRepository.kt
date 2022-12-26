package com.denisrebrof.springboottest.user

import com.denisrebrof.springboottest.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface IUserRepository : JpaRepository<User, String> {
    fun findUserByUsername(username: String): List<User>
    fun findUserById(userId: Long): User
    fun countUsersByUsername(username: String): Long
}