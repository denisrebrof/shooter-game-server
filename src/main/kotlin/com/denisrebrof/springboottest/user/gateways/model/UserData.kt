package com.denisrebrof.springboottest.user.gateways.model

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