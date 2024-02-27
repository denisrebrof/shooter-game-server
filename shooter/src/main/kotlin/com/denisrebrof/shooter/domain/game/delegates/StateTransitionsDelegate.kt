package com.denisrebrof.shooter.domain.game.delegates

import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.game.iterators.withSpawnIterator
import com.denisrebrof.shooter.domain.model.*

class StateTransitionsDelegate(
    private val settings: ShooterGameSettings,
    private val syncBotsDelegate: SyncBotsDelegate
) {
    fun createPlayingState(preparing: Preparing): PlayingState = withSpawnIterator(settings::getSpawnPos) {
        val createNextPlayingState: (PlayerTeam) -> Playing =
            { team -> nextSpawn(team).let(::createPlayerPlayingState) }
        val players = preparing.pendingPlayers.mapValues { (_, data) ->
            val dynamicState = createNextPlayingState(data.team)
            return@mapValues ShooterPlayerState(data, 0L, dynamicState)
        }
        val getPlayersCount: (PlayerTeam) -> Int = {
            players
                .filterValues { state -> state.data.team == it }
                .size
        }
        return@withSpawnIterator PlayingState(
            players = players,
            bots = syncBotsDelegate.update(
                botsMap = mapOf(),
                getPlayersCount = getPlayersCount,
                getNextSpawn = this::nextSpawn
            ),
            teamData = lastSpawnIndices
                .mapValues { (_, lastSpawnIndex) -> TeamPlayingData(lastSpawnIndex) }
        )
    }

    fun createFinishedState(playing: PlayingState): Finished = Finished(
        finishedPlayers = playing
            .players
            .mapValues { (_, data) -> data.data },
        finishedBots = playing
            .bots
            .mapValues { (_, data) -> data.playerState.data },
        winnerTeam = playing
            .teamData
            .maxBy { (_, data) -> data.kills }
            .key,
        teamKills = playing
            .teamData
            .mapValues { (_, data) -> data.kills }
    )

    private fun createPlayerPlayingState(pos: Transform) = Playing(
        hp = settings.defaultHp,
        transform = pos,
        verticalLookAngle = 0f,
        crouching = false,
        aiming = false
    )
}