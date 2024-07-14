package com.denisrebrof.shooter.domain.game.delegates

import arrow.optics.copy
import arrow.optics.dsl.index
import arrow.optics.typeclasses.Index
import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.game.iterators.withSpawnIterator
import com.denisrebrof.shooter.domain.model.*

class RevivePlayerDelegate(
    private val settings: ShooterGameSettings
) {
    fun revivePlayer(
        current: PlayingState,
        playerId: Long
    ) = current.copy {
        val playerState = PlayingState.getPlayerStateOptional(playerId)
        val playerTeam = playerState.data.team.getOrNull(current) ?: return@copy

        val isPlaying = playerState.dynamicState.playing.isEmpty(current)
        if (!isPlaying)
            return@copy

        var spawnPos = Transform.Zero
        current.withSpawnIterator(settings::getSpawnPos) {
            spawnPos = nextSpawn(playerTeam)
            playerState.dynamicState set Playing(
                hp = settings.defaultHp,
                transform = spawnPos,
                verticalLookAngle = 0f,
                crouching = false,
                aiming = false
            )

            val lastSpawnIndex = getLastSpawnIndex(playerTeam)
            PlayingState.teamData.index(Index.map(), playerTeam).lastSpawnIndex set lastSpawnIndex
        }

        inside(PlayingState.getBotStateOptional(playerId)) {
            ShooterBotState.nullableRoutePointIndex set null
            ShooterBotState.nullableRouteIndex set settings.mapSettings.findCloseRouteIndex(spawnPos, playerTeam)
        }
    }
}