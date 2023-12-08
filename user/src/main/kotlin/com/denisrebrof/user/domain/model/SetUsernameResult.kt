package com.denisrebrof.user.domain.model

enum class SetUsernameResult(val code: Long) {
    Success(0L),
    NotAvailable(1L),
    Error(2L),
}