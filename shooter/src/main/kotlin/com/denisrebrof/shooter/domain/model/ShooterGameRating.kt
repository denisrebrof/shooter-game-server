package com.denisrebrof.shooter.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ShooterGameRating(
    val userId: Long,
    val position: Int,
    val rating: Int
)
