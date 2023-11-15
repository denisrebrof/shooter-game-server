package com.denisrebrof.shooter.domain

import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.PlayerTeam

class ShooterSpawnIterator(
    private val redTeamSpawnPos: List<Transform>,
    private val blueTeamSpawnPos: List<Transform>,
) {
    private var spawnIteratorBlueCache: Iterator<Transform>? = null
    private var spawnIteratorRedCache: Iterator<Transform>? = null

    private val nextBlueSpawn: Transform
        get() {
            if (spawnIteratorBlueCache?.hasNext() != true) {
                spawnIteratorBlueCache = blueTeamSpawnPos.iterator()
            }

            return spawnIteratorBlueCache!!.next()
        }

    private val nextRedSpawn: Transform
        get() {
            if (spawnIteratorRedCache?.hasNext() != true) {
                spawnIteratorRedCache = redTeamSpawnPos.iterator()
            }

            return spawnIteratorRedCache!!.next()
        }

    fun fetchNextSpawn(team: PlayerTeam) = when (team) {
        PlayerTeam.Red -> nextRedSpawn
        PlayerTeam.Blue -> nextBlueSpawn
    }
}