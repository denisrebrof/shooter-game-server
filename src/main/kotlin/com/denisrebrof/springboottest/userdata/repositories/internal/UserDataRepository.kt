package com.denisrebrof.springboottest.userdata.repositories.internal

import com.denisrebrof.springboottest.userdata.model.UserData
import org.springframework.data.jpa.repository.JpaRepository

interface UserDataRepository : JpaRepository<UserData, Long> {
    fun findUserDataByUsername(username: String): List<UserData>
    fun findUserDataById(userId: Long): UserData?
    fun findUserDataByLocalId(userId: String): UserData?
    fun findUserDataByYandexId(yandexId: String): UserData?
}