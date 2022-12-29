package com.denisrebrof.springboottest.userdata.domain.model

import com.denisrebrof.springboottest.user.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val username: String
) {
    companion object {
        fun fromUser(user: User) = UserData(
            username = user.username
        )
    }
}