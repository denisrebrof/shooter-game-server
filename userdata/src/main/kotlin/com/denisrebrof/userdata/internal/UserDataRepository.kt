package com.denisrebrof.userdata.internal

import com.denisrebrof.userdata.model.UserData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface UserDataRepository : JpaRepository<UserData, Long> {
    fun findUserDataByUsername(username: String): List<UserData>
    fun findUserDataById(userId: Long): UserData?
    fun findUserDataByLocalId(userId: String): UserData?
    fun findUserDataByYandexId(yandexId: String): UserData?
}