package com.denisrebrof.shooter.domain.repositories

interface IShooterGamePlayerStatsRepository {
    fun handleMatchResults(userId: Long, won: Boolean, kills: Int, death: Int)
}