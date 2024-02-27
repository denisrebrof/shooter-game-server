package com.denisrebrof.shooter.domain.model

enum class PlayerTeam(val id: Int) {
    Red(1),
    Blue(2)
}

fun <T : Any> Map<PlayerTeam, T>.withEmptyTeams(default: T): Map<PlayerTeam, T> {
    val teams = PlayerTeam.values()
    val hasAllTeams = teams.all(this::containsKey)
    if (hasAllTeams)
        return this

    return toMutableMap().apply {
        teams
            .filterNot(this::containsKey)
            .forEach { set(it, default) }
    }
}