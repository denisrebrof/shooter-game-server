package com.denisrebrof.shooter.domain

import arrow.optics.Every
import arrow.optics.Optional
import arrow.optics.copy
import arrow.optics.dsl.every
import arrow.optics.dsl.index
import arrow.optics.typeclasses.Index
import com.denisrebrof.games.MVIGameHandler
import com.denisrebrof.matches.domain.model.IParticipantsHandler
import com.denisrebrof.shooter.domain.model.*
import com.denisrebrof.shooter.domain.model.PlayerTeam.Blue
import com.denisrebrof.shooter.domain.model.PlayerTeam.Red
import com.denisrebrof.utils.associateWithNotNull
import com.denisrebrof.utils.subscribeDefault
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit
import com.denisrebrof.shooter.domain.model.ShooterGameActions as Action
import com.denisrebrof.shooter.domain.model.ShooterGameIntents as Intent
import com.denisrebrof.shooter.domain.model.ShooterGameState as State

class ShooterGame private constructor(
    val settings: ShooterGameSettings,
    players: Map<Long, ShooterPlayerData>
) : MVIGameHandler<State, Intent, Action>(
    initialState = Preparing(players)
), IParticipantsHandler {

    private val spawnIterator = ShooterSpawnIterator(
        redTeamSpawnPos = settings.redTeamSpawnPos,
        blueTeamSpawnPos = settings.blueTeamSpawnPos
    )

    fun start() = stateFlow
        .ofType(Preparing::class.java)
        .firstElement()
        .delay(settings.prepareDelay, TimeUnit.MILLISECONDS)
        .map(::createPlayingState)
        .doAfterSuccess(::setState)
        .flatMap {
            val playingStateHandler = createPlayingStateHandler()
            return@flatMap Completable
                .timer(settings.gameDuration, TimeUnit.MILLISECONDS)
                .toSingle { state }
                .doFinally { playingStateHandler.dispose() }
                .toMaybe()
        }
        .ofType(PlayingState::class.java)
        .map(::createFinishedState)
        .doAfterSuccess(::setState)
        .delay(settings.completeDelay, TimeUnit.MILLISECONDS)
        .subscribeDefault { send(Action.LifecycleCompleted) }
        .let(::add)

    override fun onIntentReceived(intent: Intent) = when (intent) {
        is Intent.SelectWeapon -> selectWeapon(intent)
        is Intent.Shoot -> shoot(intent)
        is Intent.UpdatePos -> updatePos(intent)
        is Intent.Hit -> hit(intent)
    }

    override fun addPlayers(vararg players: Long) {
        state.copyAndSet {
            State.playingState.players transform { it + createJoinedPlayersStateMap(*players) }
            State.preparing.pendingPlayers transform { it + createPendingPlayerStateMap(*players) }
        }
        refreshBotsState()
        players.map { Action.JoinedStateChange(it, true) }.forEach(::send)
    }

    override fun removePlayers(vararg players: Long) {
        state.copyAndSet {
            State.playingState.players transform { playerStateMap ->
                val playerStateMutableMap = playerStateMap.toMutableMap()
                players.forEach(playerStateMutableMap::remove)
                return@transform playerStateMutableMap
            }
            State.preparing.pendingPlayers transform { playerStateMap ->
                val playerStateMutableMap = playerStateMap.toMutableMap()
                players.forEach(playerStateMutableMap::remove)
                return@transform playerStateMutableMap
            }
        }
        refreshBotsState()
        players.map { Action.JoinedStateChange(it, false) }.forEach(::send)
    }

    private fun selectWeapon(intent: Intent.SelectWeapon) = state.copyAndSet {
        getPlayerStateOptional(intent.playerId).selectedWeaponId set intent.weaponId
    }

    private fun hit(intent: Intent.Hit) = state.copyAndSet {
        val shooter = getPlayerStateOptional(intent.shooterId)
        val shooterPlaying = shooter.dynamicState.playing
        if (shooterPlaying.isEmpty(state))
            return@copyAndSet

        val receiverId = intent.receiverId ?: return@copyAndSet
        val receiver = getPlayerStateOptional(receiverId)
        val receiverPlaying = receiver.dynamicState.playing
        val receiverState = receiverPlaying.getOrNull(state) ?: return@copyAndSet
        val newHp = receiverState.hp - intent.damage
        val killed = newHp < 1
        Action.Hit(intent.shooterId, receiverId, intent.damage, killed).let(::send)
        if (!killed)
            return@copyAndSet receiverPlaying.hp set newHp

        shooter.data.kills transform { it + 1 }
        receiver.data.death transform { it + 1 }
        receiver.dynamicState set Killed(receiverState.transform)

        val killerTeam = shooter.data.team.getOrNull(state)
        val killerTeamCounter = when (killerTeam) {
            Red -> State.playingState.redTeamKills
            else -> State.playingState.blueTeamKills
        }
        killerTeamCounter transform { it + 1 }

        Completable
            .timer(settings.respawnDelay, TimeUnit.MILLISECONDS)
            .doOnComplete { revivePlayer(receiverId) }
            .subscribeDefault()
            .let(::add)
    }

    private fun shoot(intent: Intent.Shoot) = state.copyAndSet {
        val shooter = getPlayerStateOptional(intent.shooterId)
        val shooterPlaying = shooter.dynamicState.playing
        if (shooterPlaying.isEmpty(state))
            return@copyAndSet

        shooter.selectedWeaponId set intent.weaponId
        Action.Shoot(intent.shooterId, intent.weaponId, intent.direction).let(::send)
    }

    private fun revivePlayer(playerId: Long) = state.copyAndSet {
        val playerState = getPlayerStateOptional(playerId)
        val playerTeam = playerState.data.team.getOrNull(state) ?: return@copyAndSet
        val dynamicState = playerState.dynamicState.getOrNull(state) ?: return@copyAndSet

        if (dynamicState !is Killed)
            return@copyAndSet

        val revivedState = createPlayerPlayingState(playerTeam)
        playerState.dynamicState set revivedState

        getBotStateOptional(playerId) transform { state ->
            state.copy(
                routePointIndex = null,
                routeIndex = settings.botSettings.findCloseRouteIndex(revivedState.transform, playerTeam)
            )
        }
    }

    private fun updatePos(intent: Intent.UpdatePos) = state.copyAndSet {
        getPlayerPlayingStateOptional(intent.playerId).apply {
            transform set intent.pos
            verticalLookAngle set intent.verticalLookAngle
        }
    }

    private fun getPlayerStateOptional(playerId: Long): Optional<State, ShooterPlayerState> = when {
        playerId > 0 -> State
            .playingState
            .players
            .index(Index.map(), playerId)

        else -> getBotStateOptional(playerId).playerState
    }

    private fun getBotStateOptional(playerId: Long): Optional<State, ShooterBotState> = State
        .playingState
        .bots
        .index(Index.map(), playerId)

    private fun getPlayerPlayingStateOptional(playerId: Long) = getPlayerStateOptional(playerId)
        .dynamicState
        .playing

    private fun createFinishedState(playing: PlayingState): Finished = Finished(
        finishedPlayers = playing
            .players
            .mapValues { (_, data) -> data.data },
        finishedBots = playing
            .bots
            .mapValues { (_, data) -> data.playerState.data },
        winnerTeam = when {
            playing.redTeamKills > playing.blueTeamKills -> Red
            else -> Blue
        },
        redTeamKills = playing.redTeamKills,
        blueTeamKills = playing.blueTeamKills
    )

    private fun createPlayingState(preparing: Preparing) = PlayingState(
        players = preparing.pendingPlayers.mapValues { (_, data) ->
            val dynamicState = createPlayerPlayingState(data.team)
            ShooterPlayerState(data, 0L, dynamicState)
        },
        bots = createBotsStateMap(preparing.pendingPlayers.values).mapValues { (_, data) ->
            val dynamicState = createPlayerPlayingState(data.team)
            val playerState = ShooterPlayerState(data, settings.botSettings.defaultWeaponId, dynamicState)
            val routeIndex = settings.botSettings.findCloseRouteIndex(dynamicState.transform, data.team)
            return@mapValues ShooterBotState(playerState, routeIndex, 0)
        }
    )

    private fun refreshBotsState() = state.copyAndSet {
        val currentState = state
        if (currentState !is PlayingState)
            return@copyAndSet

        val newBotsCount = settings.botSettings.fillWithBotsToParticipantsCount - currentState.players.size
        var botsCountUpdate = newBotsCount - currentState.bots.size
        if (botsCountUpdate == 0)
            return@copyAndSet

        val teamToPlayersCount = currentState
            .players
            .values
            .groupBy { it.data.team }
            .mapValues { (_, players) -> players.size }
            .toMutableMap()

        val newBots = currentState.bots.toMutableMap()
        while (botsCountUpdate != 0) {
            val addOrRemove = botsCountUpdate > 0
            if (addOrRemove) {
                val minTeam = teamToPlayersCount
                    .minBy { (_, playersCount) -> playersCount }
                    .key
                val id = newBots.keys.minOrNull() ?: -1L
                newBots[id - 1] = createJoinedBotState(minTeam) ?: continue
                botsCountUpdate--
            } else {
                val maxTeam = teamToPlayersCount
                    .minBy { (_, playersCount) -> playersCount }
                    .key
                val botToRemove = newBots
                    .entries
                    .firstOrNull { it.value.playerState.data.team == maxTeam }
                    ?.key
                    ?: newBots.entries.firstOrNull()?.key
                botToRemove.let(newBots::remove)
                botsCountUpdate++
            }
        }
        State.playingState.bots set newBots
    }

    private fun createPendingPlayerStateMap(vararg players: Long): Map<Long, ShooterPlayerData> = players
        .toList()
        .associateWithNotNull { createPendingPlayerData() }

    private fun createBotsStateMap(players: Collection<ShooterPlayerData>): Map<Long, ShooterPlayerData> {
        val botsCount = settings.botSettings.fillWithBotsToParticipantsCount - players.size
        if (botsCount <= 0)
            return mapOf()

        val teamToPlayersCount = players
            .groupBy(ShooterPlayerData::team)
            .mapValues { (_, players) -> players.size }
            .toMutableMap()

        PlayerTeam
            .values()
            .filterNot(teamToPlayersCount::containsKey)
            .forEach { teamToPlayersCount[it] = 0 }

        var counter = 1L
        val botsMap = mutableMapOf<Long, ShooterPlayerData>()
        repeat(botsCount) {
            val minTeam = teamToPlayersCount
                .minBy { (_, playersCount) -> playersCount }
                .key

            teamToPlayersCount[minTeam] = (teamToPlayersCount[minTeam] ?: 0) + 1
            botsMap[counter++ * -1] = ShooterPlayerData(minTeam, 0, 0)
        }
        return botsMap
    }

    private fun createJoinedPlayersStateMap(
        vararg players: Long
    ): Map<Long, ShooterPlayerState> = players
        .toList()
        .associateWithNotNull { createJoinedPlayerState() }

    private fun createPendingPlayerData() = getPendingPlayerTeam()?.let(::ShooterPlayerData)

    private fun createJoinedPlayerState(): ShooterPlayerState? {
        val playerTeam = getJoinedPlayerTeam() ?: return null
        return createJoinedPlayerState(playerTeam)
    }

    private fun createJoinedBotState(team: PlayerTeam): ShooterBotState {
        val data = ShooterPlayerData(team)
        val dynamicState = createPlayerPlayingState(team)

        val playerState = ShooterPlayerState(data, settings.botSettings.defaultWeaponId, dynamicState)
        val routeIndex = settings.botSettings.findCloseRouteIndex(dynamicState.transform, data.team)
        return ShooterBotState(playerState, routeIndex, 0)
    }

    private fun createJoinedPlayerState(team: PlayerTeam): ShooterPlayerState {
        val data = ShooterPlayerData(team)
        val dynamicState = createPlayerPlayingState(team)
        return ShooterPlayerState(data, 0L, dynamicState)
    }

    private fun getPendingPlayerTeam(): PlayerTeam? {
        val players = State.preparing.pendingPlayers.getOrNull(state)?.values ?: return null
        val teamsToPlayerCount = players
            .groupBy { player -> player.team }
            .mapValues { (_, teamPlayers) -> teamPlayers.size }
        return teamsToPlayerCount
            .minByOrNull { (_, playerCount) -> playerCount }
            ?.key
            ?: Red
    }

    private fun getJoinedPlayerTeam(): PlayerTeam? {
        val players = State.playingState.players.getOrNull(state)?.values ?: return null
        val bots = State.playingState.bots.getOrNull(state)?.values?.map(ShooterBotState::playerState) ?: listOf()
        val participants = players + bots
        val redTeamCount = participants.count { player -> player.data.team == Red }
        val blueTeamCount = participants.size - redTeamCount
        return if (redTeamCount > blueTeamCount) Blue else Red
    }

    private fun createPlayerPlayingState(team: PlayerTeam) = Playing(
        hp = 100,
        transform = spawnIterator.fetchNextSpawn(team),
        verticalLookAngle = 0f,
        crouching = false,
        aiming = false
    )

    private fun createPlayingStateHandler(): Disposable = Flowable
        .interval(settings.botSettings.botUpdateLoopDelayMs, TimeUnit.MILLISECONDS)
        .onBackpressureDrop()
        .subscribeDefault { updateBots() }

    private fun updateBots() = state.copyAndSet {
        State.playingState.bots.every(Every.map()) transform (::updateBot)
    }

    private fun updateBot(botState: ShooterBotState) = botState.copy {
        val playing = ShooterBotState.playerState.dynamicState.playing.getOrNull(botState) ?: return@copy
        val routes = when (botState.playerState.data.team) {
            Red -> settings.botSettings.redTeamRoutes
            else -> settings.botSettings.blueTeamRoutes
        }
        val currentRoute = botState.routeIndex?.let(routes::getOrNull) ?: return@copy
        val currentPointIndex = botState.routePointIndex ?: if (currentRoute.isNotEmpty()) 0 else null ?: return@copy
        val nextPointIndex = if (currentRoute.lastIndex > currentPointIndex) currentPointIndex + 1 else return@copy
        val nextPoint = currentRoute[nextPointIndex]

        val translation = settings.botSettings.botSpeed * settings.botSettings.botUpdateLoopDelayMs * 0.001f
        val position = playing.transform.translateTo(nextPoint, translation)
        ShooterBotState.playerState.dynamicState.playing.transform set position

        val nextPointReached = nextPoint.isClose(position, settings.botSettings.botReachWaypointDist)
        if (nextPointReached) {
            if (currentRoute.lastIndex > nextPointIndex) {
                ShooterBotState.routePointIndex set nextPointIndex
            }
        }
    }

    companion object {
        fun create(
            playerIds: List<Long>,
            settings: ShooterGameSettings
        ): ShooterGame {
            val teams = listOf(Red, Blue)
            val players = playerIds
                .mapIndexed { index, playerId ->
                    val team = teams[index % teams.size]
                    playerId to ShooterPlayerData(team)
                }
                .toMap()
            return ShooterGame(settings, players)

        }
    }
}