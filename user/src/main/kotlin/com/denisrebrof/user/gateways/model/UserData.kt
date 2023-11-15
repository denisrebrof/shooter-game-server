package com.denisrebrof.user.gateways.model

import com.denisrebrof.user.domain.model.User
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