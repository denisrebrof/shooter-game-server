package com.denisrebrof.springboottest.matches.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Match(
    val id: String,
    val createdTime: Long,
    val participantIds: List<Long>
)