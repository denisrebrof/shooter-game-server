package com.denisrebrof.springboottest.user.domain.repositories

import com.denisrebrof.springboottest.user.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface IUserRepository : JpaRepository<User, String> {
    fun findUserByUsername(username: String): List<User>
    fun findUserById(userId: Long): User?
    fun findUserByLocalId(userId: String): User?
    fun findUserByYandexId(yandexId: String): User?
    fun countUsersByUsername(username: String): Long
}