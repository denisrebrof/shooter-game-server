package com.denisrebrof.sringboottest.data

import com.denisrebrof.sringboottest.data.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface IUserRepository : JpaRepository<User, String> {
}