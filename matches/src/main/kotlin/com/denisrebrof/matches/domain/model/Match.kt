package com.denisrebrof.matches.domain.model

data class Match(
    val id: String,
    val mapId: Int,
    val participants: Set<Long>,
    val minParticipants: Int,
    val createdTime: Long,
)