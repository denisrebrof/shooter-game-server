package com.denisrebrof.shooter.domain.game.delegates

import com.denisrebrof.shooter.domain.game.PlayerStateFactory
import com.denisrebrof.shooter.domain.game.iterators.createSpawnIterator
import com.denisrebrof.shooter.domain.game.iterators.createTeamIterator
import com.denisrebrof.shooter.domain.model.PlayingState
import com.denisrebrof.shooter.domain.model.Preparing
import com.denisrebrof.shooter.domain.model.ShooterGameSettings
import com.denisrebrof.shooter.domain.model.ShooterPlayerData

class AddPlayersDelegate(
    private val settings: ShooterGameSettings,
    private val playerStateFactory: PlayerStateFactory,
    private val syncBotsDelegate: SyncBotsDelegate
) {
    fun addPlayers(
        current: PlayingState,
        vararg playerIds: Long
    ): PlayingState = with(current) {
        val spawnIterator = createSpawnIterator(settings::getSpawnPos)
        val teamIterator = createTeamIterator()
        val addedPlayers = playerIds.toList().associateWith {
            val team = teamIterator.nextTeam()
            val spawn = spawnIterator.nextSpawn(team)
            return@associateWith playerStateFactory.createJoinedPlayerState(team, spawn)
        }
        val newPlayers = players + addedPlayers
        val newBots = syncBotsDelegate.update(
            botsMap = bots,
            getPlayersCount = teamIterator::getPlayerCount,
            getNextSpawn = spawnIterator::nextSpawn
        )
        return copy(
            players = newPlayers,
            bots = newBots
        )
    }

    fun addPlayers(
        current: Preparing,
        vararg players: Long
    ): Preparing = with(current) {
        val teamIterator = createTeamIterator()
        val nextPlayerData: (Long) -> ShooterPlayerData = {
            teamIterator
                .nextTeam()
                .let(::ShooterPlayerData)
        }
        val newPlayers = players
            .toList()
            .associateWith(nextPlayerData)
            .let(pendingPlayers::plus)
        return copy(pendingPlayers = newPlayers)
    }
}