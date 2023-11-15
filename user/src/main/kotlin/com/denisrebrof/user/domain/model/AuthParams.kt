package com.denisrebrof.user.domain.model

sealed class AuthParams {
    data class UsernamePassword(
        val username: String,
        val password: String
    ) : AuthParams()

    data class YandexId(val id: String) : AuthParams()

    data class Anonymous(val id: String) : AuthParams()

    data class Token(val token: String) : AuthParams()
}