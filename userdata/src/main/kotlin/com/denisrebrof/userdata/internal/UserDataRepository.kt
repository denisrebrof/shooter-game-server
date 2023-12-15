package com.denisrebrof.userdata.internal

import com.denisrebrof.userdata.model.UserData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

interface UserDataRepository : JpaRepository<UserData, Long> {
    fun findUserDataByUsername(username: String): List<UserData>
    fun findUserDataById(userId: Long): UserData?
    fun findUserDataByLocalId(userId: String): UserData?
    fun findUserDataByYandexId(yandexId: String): UserData?

    @Query(
        "SELECT u FROM UserData u "
//            + "WHERE CASE WHEN ?3 = TRUE THEN (u.rating>=?2) ELSE (u.rating<=?2) END "
//            + "ORDER BY CASE WHEN ?3 = TRUE THEN u.rating ELSE -u.rating END"
//            + ", CASE WHEN ?3 = TRUE THEN u.id ELSE -u.id END"
    )
    fun getUsersAroundByRating(userId: Long, userRating: Int, findGreater: Boolean): List<UserData>
}