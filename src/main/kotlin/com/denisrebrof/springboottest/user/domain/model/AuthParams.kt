package com.denisrebrof.springboottest.user.domain.model

sealed class AuthParams {
    data class UsernamePassword(
        val username: String,
        val password: String
    ) : AuthParams()

    data class YandexId(val id: String) : AuthParams()

    object Anonymous : AuthParams()

    data class Token(val token: String) : AuthParams()
}