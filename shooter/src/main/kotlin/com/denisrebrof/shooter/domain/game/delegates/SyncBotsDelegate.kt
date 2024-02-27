package com.denisrebrof.shooter.domain.game.delegates

import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.game.PlayerStateFactory
import com.denisrebrof.shooter.domain.model.PlayerTeam
import com.denisrebrof.shooter.domain.model.ShooterBotState
import com.denisrebrof.shooter.domain.model.ShooterGameSettings
import kotlin.math.absoluteValue

class SyncBotsDelegate(
    private val settings: ShooterGameSettings,
    private val playerStateFactory: PlayerStateFactory
) {
    fun update(
        botsMap: Map<Long, ShooterBotState>,
        getPlayersCount: (PlayerTeam) -> Int?,
        getNextSpawn: (PlayerTeam) -> Transform
    ): Map<Long, ShooterBotState> {
        val bots = botsMap.toMutableMap()
        PlayerTeam.values().forEach { team ->
            val playersCount = getPlayersCount(team) ?: return@forEach
            val teamBotIds = bots
                .filterValues { state -> state.playerState.data.team == team }
                .keys

            val updates = getBotUpdates(teamBotIds, playersCount)
            if (updates == BotsUpdate.None)
                return@forEach

            if (updates is BotsUpdate.Remove)
                return@forEach updates.ids.forEach(bots::remove)

            if (updates is BotsUpdate.Create) {
                val minBotId = bots
                    .keys
                    .minOrNull()
                    ?.minus(1)
                    ?: -1L

                val createBot: (Long) -> ShooterBotState = {
                    playerStateFactory.createJoinedBotState(
                        team = team,
                        pos = getNextSpawn(team)
                    )
                }

                minBotId
                    .downTo(minBotId - updates.count + 1)
                    .associateWith(createBot)
                    .forEach(bots::put)
            }
        }
        return bots
    }

    private fun getBotUpdates(
        botIds: Set<Long>,
        playersCount: Int
    ): BotsUpdate {
        val botUpdates = settings
            .botSettings
            .fillWithBotsToTeamSize
            .minus(playersCount)
            .coerceAtLeast(0)
            .minus(botIds.size)

        return when {
            botUpdates == 0 -> BotsUpdate.None
            botUpdates > 0 -> BotsUpdate.Create(botUpdates)
            else -> botIds
                .take(botUpdates.absoluteValue)
                .toSet()
                .let(BotsUpdate::Remove)
        }
    }

    private sealed class BotsUpdate {
        object None : BotsUpdate()
        data class Create(val count: Int) : BotsUpdate()
        data class Remove(val ids: Set<Long>) : BotsUpdate()
    }
}