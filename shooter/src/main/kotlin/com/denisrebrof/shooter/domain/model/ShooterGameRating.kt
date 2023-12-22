package com.denisrebrof.shooter.domain.model

import com.denisrebrof.user.domain.model.User

data class ShooterGameRating(
    val user: User,
    val position: Long,
    val rating: Int
)
