package com.denisrebrof.springboottest.commands.domain.model

data class RequestData(
    val userId: Long,
    val commandId: Long,
    val data: String,
    val responseId: String = ""
)