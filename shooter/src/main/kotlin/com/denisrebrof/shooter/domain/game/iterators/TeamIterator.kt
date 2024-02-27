package com.denisrebrof.shooter.domain.game.iterators

import com.denisrebrof.shooter.domain.model.*

class TeamIterator(
    teamPlayerCount: Map<PlayerTeam, Int>,
) {
    private val teamToPlayerCount = teamPlayerCount.toMutableMap()

    fun nextTeam(): PlayerTeam {
        val (minTeam, minCount) = teamToPlayerCount.minBy { (_, count) -> count }
        teamToPlayerCount[minTeam] = minCount + 1
        return minTeam
    }

    fun getPlayerCount(team: PlayerTeam): Int? = teamToPlayerCount[team]
}

fun Preparing.createTeamIterator(): TeamIterator = pendingPlayers
    .values
    .groupBy(ShooterPlayerData::team)
    .mapValues { (_, states) -> states.size }
    .toTeamIterator()

fun PlayingState.createTeamIterator(): TeamIterator = players
    .values
    .groupBy { state -> state.data.team }
    .mapValues { (_, states) -> states.size }
    .toTeamIterator()

private fun Map<PlayerTeam, Int>.toTeamIterator(): TeamIterator = this
    .withEmptyTeams(0)
    .let(::TeamIterator)