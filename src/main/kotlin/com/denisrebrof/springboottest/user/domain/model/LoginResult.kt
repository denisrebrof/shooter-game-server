package com.denisrebrof.springboottest.user.domain.model

sealed class LoginResult {
    data class Success(val token: String) : LoginResult()
    object Failed : LoginResult()
}