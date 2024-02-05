package com.denisrebrof.shooter.domain

import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.PlayerTeam

data class ShooterGameSettings(
    val redTeamSpawnPos: List<Transform> = listOf(
        Transform(12f, 2f, 16f, 180f),
        Transform(0f, 2f, 5f, 180f),
        Transform(13f, 2f, 16f, 180f),
        Transform(1f, 2f, 5f, 180f),
        Transform(14f, 2f, 16f, 180f),
        Transform(2f, 2f, 5f, 180f),
        Transform(16f, 2f, 16f, 180f),
        Transform(3f, 2f, 5f, 180f),
        Transform(17f, 2f, 16f, 180f),
        Transform(4f, 2f, 5f, 180f),
        Transform(18f, 2f, 16f, 180f),
        Transform(5f, 2f, 5f, 180f),
        Transform(19f, 2f, 16f, 180f),
        Transform(6f, 2f, 5f, 180f),
    ),

    val blueTeamSpawnPos: List<Transform> = listOf(
        Transform(13f, 2f, -45f, 0f),
        Transform(27f, -2f, -32f, 0f),
        Transform(14f, 2f, -45f, 0f),
        Transform(28f, -2f, -32f, 0f),
        Transform(15f, 2f, -45f, 0f),
        Transform(29f, -2f, -32f, 0f),
        Transform(16f, 2f, -45f, 0f),
        Transform(30f, -2f, -32f, 0f),
        Transform(17f, 2f, -45f, 0f),
        Transform(31f, -2f, -32f, 0f),
        Transform(18f, 2f, -45f, 0f),
        Transform(32f, -2f, -32f, 0f),
        Transform(19f, 2f, -45f, 0f),
        Transform(33f, -2f, -32f, 0f),
        Transform(20f, 2f, -45f, 0f),
        Transform(34f, -2f, -32f, 0f),
        Transform(21f, 2f, -45f, 0f),
        Transform(35f, -2f, -32f, 0f),
    ),
    val botSettings: BotSettings,
    val respawnDelay: Long,
    val prepareDelay: Long,
    val gameDuration: Long,
    val completeDelay: Long,
) {

    data class BotSettings(
        val botSpeed: Float = 4f,
        val botReachWaypointDist: Float = 0.5f,
        val botUpdateLoopDelayMs: Long = 100L,
        val defaultWeaponId: Long = 1L,
        val fillWithBotsToParticipantsCount: Int = 0,
        val redTeamRoutes: List<List<Transform>> = listOf(
            listOf(
                Transform(12f, 2f, 16f, 180f),
            ),
        ),
        val blueTeamRoutes: List<List<Transform>> = listOf(
            listOf(
                Transform(15.16f, 0.6f, -43.8f, 0f),
                Transform(15.76f, 0.6f, -39.91f, 0f),
                Transform(4.39f, 0.6f, -39.79f, 0f),
                Transform(0.2700005f, 0.6f, -35.25f, 0f),
                Transform(-0.1100006f, 0.6f, -28.21f, 0f),
                Transform(0.5699997f, 0.6f, -22.77f, 0f),
                Transform(3.91f, 0.6f, -19.42f, 0f),
                Transform(3.97f, 0.6f, -8.029999f, 0f),
            ),
            listOf(
                Transform(16.66f, 0.6f, -43.64f, 0f),
                Transform(16.93f, 0.6f, -40.17f, 0f),
                Transform(19.91f, 0.6f, -39.55f, 0f),
                Transform(29.97f, 0.6f, -39.89f, 0f),
                Transform(40.13f, 0.6f, -40.11f, 0f),
                Transform(40.66f, 0.6f, -24.39f, 0f),
                Transform(28.23f, 0.6f, -22.09f, 0f),
                Transform(28.546f, 0.6f, 2.470001f, 0f),
            ),
            listOf(
                Transform(30.52f, -2.65f, -31.44f, 0f),
                Transform(21.66f, -2.65f, -31.2f, 0f),
                Transform(12.623f, -2.65f, -30.204f, 0f),
                Transform(12.48f, -2.65f, -23.44f, 0f),
                Transform(11.844f, -2.65f, -15.619f, 0f),
                Transform(16.3f, -2.65f, -15.92f, 0f),
                Transform(16.13f, -2.65f, -8f, 0f),
                Transform(5.338f, 1.342f, -8.369999f, 0f),
            ),
            listOf(
                Transform(33.75f, -2.65f, -31.93f, 0f),
                Transform(30.53f, -2.65f, -30.19f, 0f),
                Transform(25.45f, -2.65f, -30.69f, 0f),
                Transform(21.16f, -2.65f, -29.24f, 0f),
                Transform(19.65f, -2.65f, -29.01f, 0f),
                Transform(20.38f, 0.99f, -21.09f, 0f),
                Transform(27.217f, 0.6f, -21.75f, 0f),
                Transform(27.65f, 0.6f, 2.470001f, 0f),
            ),
        )
    ) {
        fun findCloseRouteIndex(origin: Transform, team: PlayerTeam): Int? = when (team) {
            PlayerTeam.Red -> redTeamRoutes.findCloseRouteIndex(origin)
            PlayerTeam.Blue -> blueTeamRoutes.findCloseRouteIndex(origin)
        }

        private fun List<List<Transform>>.findCloseRouteIndex(origin: Transform): Int? {
            println("findCloseRouteIndex for $origin")
            if (isEmpty())
                return null

            if (size == 1)
                return 0

            var minDist = Float.MAX_VALUE
            var closestRouteIndex: Int? = null

            forEachIndexed { index, route ->
                val distance = route
                    .firstOrNull()
                    ?.getSquaredDistanceTo(origin)
                    ?: return@forEachIndexed

                if (minDist <= distance)
                    return@forEachIndexed

                minDist = distance
                closestRouteIndex = index
            }

            return closestRouteIndex
        }
    }
}