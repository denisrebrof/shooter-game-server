package com.denisrebrof.shooter.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ShooterGamePlayerStats(
    val kills: Int,
    val death: Int
)
