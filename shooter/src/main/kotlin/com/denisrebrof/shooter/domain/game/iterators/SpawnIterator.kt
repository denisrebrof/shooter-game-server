package com.denisrebrof.shooter.domain.game.iterators

import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.PlayerTeam
import com.denisrebrof.shooter.domain.model.PlayingState

class SpawnIterator(
    spawnIndices: Map<PlayerTeam, Int> = mapOf(),
    private val getSpawnPos: (PlayerTeam) -> List<Transform>,
) {

    private val teamToSpawnIndex = spawnIndices.toMutableMap()

    val lastSpawnIndices: Map<PlayerTeam, Int>
        get() = teamToSpawnIndex

    fun nextSpawn(team: PlayerTeam): Transform {
        val spawns = getSpawnPos(team)
        val currentIndex = teamToSpawnIndex[team] ?: 0
        val nextSpawnIndex = when {
            spawns.lastIndex > currentIndex -> currentIndex.plus(1)
            else -> 0
        }
        teamToSpawnIndex[team] = nextSpawnIndex
        return spawns[nextSpawnIndex]
    }

    fun getLastSpawnIndex(team: PlayerTeam): Int = teamToSpawnIndex.getOrDefault(team, 0)
}

fun PlayingState.createSpawnIterator(
    getSpawnPos: (PlayerTeam) -> List<Transform>
) = teamData
    .mapValues { (_, data) -> data.lastSpawnIndex }
    .let { spawnIndices -> SpawnIterator(spawnIndices, getSpawnPos) }

fun <T : Any> withSpawnIterator(
    getSpawnPos: (PlayerTeam) -> List<Transform>,
    spawnIndices: Map<PlayerTeam, Int> = mapOf(),
    scope: SpawnIterator.() -> T
) = SpawnIterator(spawnIndices, getSpawnPos).scope()

fun <T : Any> PlayingState.withSpawnIterator(
    getSpawnPos: (PlayerTeam) -> List<Transform>,
    scope: SpawnIterator.() -> T
) = createSpawnIterator(getSpawnPos).scope()