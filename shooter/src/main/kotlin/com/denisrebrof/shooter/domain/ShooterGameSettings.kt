package com.denisrebrof.shooter.domain

import com.denisrebrof.games.Transform

data class ShooterGameSettings(
    val redTeamSpawnPos: List<Transform> = listOf(
        Transform(-1.209999f, 17.77f, 70.65123f, 180f),
        Transform(-22.28f, 17.77f, 63.95f, 180f),
        Transform(24.37077f, 17.77f, 65.7467f, 180f)
    ),

    val blueTeamSpawnPos: List<Transform> = listOf(
        Transform(3.301947f, 17.71f, -61.26778f, 0f),
        Transform(24.37194f, 17.23f, -54.56653f, 0f),
        Transform(-22.27883f, 17.38f, -56.3633f, 0f)
    ),
    val respawnDelay: Long = 3000L,
    val prepareDelay: Long = 10000L,
    val gameDuration: Long = 100000L,
    val completeDelay: Long = 10000L,
)