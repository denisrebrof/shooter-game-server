package com.denisrebrof.shooter.domain.game.delegates

import com.denisrebrof.shooter.domain.game.iterators.createSpawnIterator
import com.denisrebrof.shooter.domain.model.PlayerTeam
import com.denisrebrof.shooter.domain.model.PlayingState
import com.denisrebrof.shooter.domain.model.Preparing
import com.denisrebrof.shooter.domain.model.ShooterGameSettings

class RemovePlayersDelegate(
    private val settings: ShooterGameSettings,
    private val syncBotsDelegate: SyncBotsDelegate
) {
    fun removePlayers(
        current: PlayingState,
        vararg playerIds: Long
    ): PlayingState = with(current) {
        val newPlayers = playerIds.toSet().let(current.players::minus)

        val getPlayersCount: (PlayerTeam) -> Int = {
            newPlayers
                .filterValues { state -> state.data.team == it }
                .size
        }

        //Bots update
        val spawnIterator = current.createSpawnIterator(settings::getSpawnPos)
        val newBots = syncBotsDelegate.update(
            botsMap = current.bots,
            getPlayersCount = getPlayersCount,
            getNextSpawn = spawnIterator::nextSpawn
        )
        return@with current.copy(
            players = newPlayers,
            bots = newBots
        )
    }

    fun removePlayers(
        current: Preparing,
        vararg playerIds: Long
    ): Preparing = with(current) {
        val newPlayers = pendingPlayers - playerIds.toSet()
        return@with copy(pendingPlayers = newPlayers)
    }
}