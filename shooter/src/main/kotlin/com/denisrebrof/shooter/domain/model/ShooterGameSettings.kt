package com.denisrebrof.shooter.domain.model

import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.PlayerTeam.Blue
import com.denisrebrof.shooter.domain.model.PlayerTeam.Red

data class ShooterGameSettings(
    val mapSettings: MapSettings,
    val botSettings: BotSettings,
    val defaultHp: Int,
    val respawnDelay: Long,
    val prepareDelay: Long,
    val gameDuration: Long,
    val completeDelay: Long,
) {
    fun getDefaultFlagPos(team: PlayerTeam) = mapSettings.getDefaultFlagPos(team)

    fun getSpawnPos(team: PlayerTeam) = mapSettings.getSpawnPos(team)

    data class BotSettings(
        val botSpeed: Float = 4f,
        val botReachWaypointDist: Float = 0.5f,
        val botUpdateLoopDelayMs: Long = 100L,
        val visibilityMaskBufferingDelay: Long = 1000L,
        val shootingMaxRange: Float = 10f,
        val defaultWeaponId: Long = 1L,
        val fillWithBotsToTeamSize: Int = 0,
    )

    data class MapSettings(
        private val redTeamSpawnPos: List<Transform>,
        private val blueTeamSpawnPos: List<Transform>,
        private val redTeamFlagPos: Transform = Transform(-33.307f, -12.36f, 31.275f, 0f),
        private val blueTeamFlagPos: Transform = Transform(20.443f, -12.36f, 31.275f, 0f),
        val redTeamRoutes: List<List<Transform>>,
        val blueTeamRoutes: List<List<Transform>>
    ) {
        fun getDefaultFlagPos(team: PlayerTeam) = when (team) {
            Red -> redTeamFlagPos
            Blue -> blueTeamFlagPos
        }

        fun getSpawnPos(team: PlayerTeam) = when (team) {
            Red -> redTeamSpawnPos
            Blue -> blueTeamSpawnPos
        }

        fun getRoutes(team: PlayerTeam) = when (team) {
            Red -> redTeamRoutes
            Blue -> blueTeamRoutes
        }

        fun findCloseRouteIndex(origin: Transform, team: PlayerTeam): Int? = when (team) {
            Red -> redTeamRoutes.findCloseRouteIndex(origin)
            Blue -> blueTeamRoutes.findCloseRouteIndex(origin)
        }

        private fun List<List<Transform>>.findCloseRouteIndex(origin: Transform): Int? {
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