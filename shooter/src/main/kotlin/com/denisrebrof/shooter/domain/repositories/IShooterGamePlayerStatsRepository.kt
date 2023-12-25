package com.denisrebrof.shooter.domain.repositories

import com.denisrebrof.shooter.domain.model.ShooterGamePlayerStats
import com.denisrebrof.shooter.domain.model.ShooterGameRating

interface IShooterGamePlayerStatsRepository {
    fun handleMatchResults(userId: Long, won: Boolean, kills: Int, death: Int)

    fun getRating(userId: Long, size: Int): List<ShooterGameRating>

    fun getPlayerStats(userId: Long): ShooterGamePlayerStats?
}