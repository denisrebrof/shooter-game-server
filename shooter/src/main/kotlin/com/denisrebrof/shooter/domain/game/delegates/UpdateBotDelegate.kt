package com.denisrebrof.shooter.domain.game.delegates

import arrow.optics.copy
import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.*
import java.util.*
import kotlin.reflect.safeCast

class UpdateBotDelegate(
    private val settings: ShooterGameSettings
) {

    private val submittedTargets = Collections.synchronizedMap(mutableMapOf<Long, Set<Long>>())

    private var targets: Map<Long, Long> = emptyMap()

    fun setTarget(botId: Long, targetId: Long) {
        submittedTargets[botId] = submittedTargets[botId]
            ?.plus(targetId)
            ?: setOf(targetId)
    }

    private fun getTarget(botId: Long): Long = targets.getOrDefault(botId, emptyBotTargetId)

    private fun createTarget(botId: Long): Long {
        val botTargets = submittedTargets[botId] ?: return getTarget(botId)
        if (botTargets.isEmpty())
            return getTarget(botId)

        val maxSubmittedTarget = botTargets
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { (_, count) -> count }
            ?.key

        return maxSubmittedTarget ?: getTarget(botId)
    }

    private fun updateTargets(botIds: Set<Long>) {
        val newTargets = botIds.associateWith(::createTarget)
        targets = newTargets
        submittedTargets.clear()
    }

    fun updateBots(state: PlayingState): UpdateBotsResult {
        updateTargets(state.bots.keys)
        val actions = mutableListOf<ShooterGameActions>()
        val hits = mutableListOf<ShooterGameIntents.Hit>()
        val newBotStates = state.bots.mapValues { (botId, bot) ->
            val targetId = getTarget(botId)
            if (targetId == emptyBotTargetId)
                return@mapValues updateBot(bot, null)

            val target = state
                .participants[targetId]
                ?.dynamicState
                ?.let(Playing::class::safeCast)
                ?: return@mapValues updateBot(bot, null)

            val targetPos = target.transform
            val weaponId = bot.playerState.selectedWeaponId
            ShooterGameActions
                .Shoot(botId, weaponId, targetPos)
                .let(actions::add)

            //if target is bot
            if (targetId < 0)
                ShooterGameIntents
                    .Hit(botId, weaponId, 10, targetPos, targetId)
                    .let(hits::add)

            updateBot(bot, targetPos)
        }

        return UpdateBotsResult(
            state = state.copy(bots = newBotStates),
            actions = actions,
            hits = hits
        )
    }

    private fun updateBot(
        botState: ShooterBotState,
        lookDir: Transform?
    ): ShooterBotState {
        val playing = ShooterBotState.playerState.dynamicState.playing.getOrNull(botState) ?: return botState
        val rotatedState: () -> ShooterBotState = rotatedState@{
            if (lookDir == null)
                return@rotatedState botState

            return@rotatedState botState.copy {
                ShooterBotState.playerState.dynamicState.playing.transform transform {
                    it.lookAt(lookDir)
                }
            }
        }
        val currentRoute = botState.getCurrentRoute() ?: return botState
        val currentPointIndex = botState.routePointIndex ?: when {
            currentRoute.isNotEmpty() -> 0
            else -> return rotatedState()
        }
        val nextPointIndex = when {
            currentRoute.lastIndex > currentPointIndex -> currentPointIndex + 1
            else -> return rotatedState()
        }
        val nextPoint = currentRoute[nextPointIndex]

        val speedMul = if (lookDir != null) 0.5f else 1f
        val translation = speedMul
            .times(0.001f)
            .times(settings.botSettings.botSpeed)
            .times(settings.botSettings.botUpdateLoopDelayMs)

        val position = playing
            .transform
            .translateTo(nextPoint, translation)
            .lookAt(lookDir ?: nextPoint)

        val nextPointReached = nextPoint.isClose(position, settings.botSettings.botReachWaypointDist)
        val routePointIndex = when {
            nextPointReached && nextPointIndex < currentRoute.lastIndex -> nextPointIndex
            else -> botState.routePointIndex
        }

        return botState.copy {
            ShooterBotState.playerState.dynamicState.playing.transform set position
            ShooterBotState.nullableRoutePointIndex set routePointIndex
        }
    }

    private fun ShooterBotState.getCurrentRoute(): List<Transform>? {
        val routes = when (playerState.data.team) {
            PlayerTeam.Red -> settings.botSettings.redTeamRoutes
            else -> settings.botSettings.blueTeamRoutes
        }
        return routeIndex?.let(routes::getOrNull)
    }

    data class UpdateBotsResult(
        val state: PlayingState,
        val actions: List<ShooterGameActions>,
        val hits: List<ShooterGameIntents.Hit>
    )
}