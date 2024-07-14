package com.denisrebrof.shooter.domain.game.delegates

import com.denisrebrof.shooter.domain.game.iterators.createSpawnIterator
import com.denisrebrof.shooter.domain.model.*
import com.denisrebrof.shooter.domain.model.TeamPlayingData.Companion.FlagIdleStateId

class RemovePlayersDelegate(
    private val settings: ShooterGameSettings,
    private val syncBotsDelegate: SyncBotsDelegate
) {
    fun removePlayers(
        current: PlayingState,
        vararg playerIds: Long
    ): PlayingState = with(current) {
        val playerIdsSet = playerIds.toSet()
        val leftPlayers = playerIdsSet.let(current.players::minus)

        val getPlayersCount: (PlayerTeam) -> Int = {
            leftPlayers
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

        val blueTeamPlayerId = teamData[PlayerTeam.Blue]?.flagPlayerId
        val redTeamPlayerId = teamData[PlayerTeam.Red]?.flagPlayerId

        val returnFlagsMap = mapOf(
            PlayerTeam.Blue to when {
                blueTeamPlayerId == null -> false
                else -> playerIdsSet.any(blueTeamPlayerId::equals)
            },
            PlayerTeam.Red to when {
                redTeamPlayerId == null -> false
                else -> playerIdsSet.any(redTeamPlayerId::equals)
            }
        )

        val newTeamData = teamData.mapValues { (team, data) ->
            val needReturnFlag = returnFlagsMap.getOrDefault(team, false)
            if (!needReturnFlag)
                return@mapValues data

            return@mapValues data.copy(
                flagStateId = FlagIdleStateId,
                flagPos = settings.getDefaultFlagPos(team)
            )
        }

        return@with copy(
            players = leftPlayers,
            bots = newBots,
            teamData = newTeamData
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