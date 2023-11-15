package com.denisrebrof.shooter.domain

import arrow.optics.dsl.index
import arrow.optics.typeclasses.Index
import com.denisrebrof.games.MVIGameHandler
import com.denisrebrof.matches.domain.model.IParticipantsHandler
import com.denisrebrof.shooter.domain.model.*
import com.denisrebrof.utils.associateWithNotNull
import com.denisrebrof.utils.subscribeDefault
import io.reactivex.rxjava3.core.Completable
import com.denisrebrof.shooter.domain.model.PlayerTeam.Blue
import com.denisrebrof.shooter.domain.model.PlayerTeam.Red
import com.denisrebrof.shooter.domain.model.ShooterGameState
import java.util.concurrent.TimeUnit
import com.denisrebrof.shooter.domain.model.ShooterGameActions as Action
import com.denisrebrof.shooter.domain.model.ShooterGameIntents as Intent
import com.denisrebrof.shooter.domain.model.ShooterGameState as State

class ShooterGame private constructor(
    private val settings: ShooterGameSettings,
    players: Map<Long, ShooterPlayerData>
) : MVIGameHandler<State, Intent, Action>(
    initialState = Preparing(players, settings.gameDuration)
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
        .delay(settings.gameDuration, TimeUnit.MILLISECONDS)
        .map { state }
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

    override fun addPlayers(vararg players: Long) = state.copyAndSet {
        ShooterGameState.playingState.players transform { it + createJoinedPlayerStateMap(*players) }
        ShooterGameState.preparing.pendingPlayers transform { it + createPendingPlayerStateMap(*players) }
    }

    override fun removePlayers(vararg players: Long) = state.copyAndSet {
        ShooterGameState.playingState.players transform { playerStateMap ->
            val playerStateMutableMap = playerStateMap.toMutableMap()
            players.forEach(playerStateMutableMap::remove)
            return@transform playerStateMutableMap
        }
        ShooterGameState.preparing.pendingPlayers transform { playerStateMap ->
            val playerStateMutableMap = playerStateMap.toMutableMap()
            players.forEach(playerStateMutableMap::remove)
            return@transform playerStateMutableMap
        }
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
            Red -> ShooterGameState.playingState.redTeamKills
            else -> ShooterGameState.playingState.blueTeamKills
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
        getPlayerStateOptional(playerId).dynamicState transform transform@{ dynamicState ->
            if (dynamicState !is Killed)
                return@transform dynamicState

            return@transform createPlayerPlayingState(playerTeam)
        }
    }

    private fun updatePos(intent: Intent.UpdatePos) = state.copyAndSet {
        getPlayerPlayingStateOptional(intent.playerId).apply {
            transform set intent.pos
            verticalLookAngle set intent.verticalLookAngle
        }
    }

    private fun getPlayerStateOptional(playerId: Long) = ShooterGameState
        .playingState
        .players
        .index(Index.map(), playerId)

    private fun getPlayerPlayingStateOptional(playerId: Long) = getPlayerStateOptional(playerId)
        .dynamicState
        .playing

    private fun createFinishedState(playing: PlayingState): Finished = Finished(
        finishedPlayers = playing
            .players
            .mapValues { (_, data) -> data.data },
        winnerTeam = when {
            playing.redTeamKills > playing.blueTeamKills -> Red
            else -> Blue
        },
        redTeamKills = playing.redTeamKills,
        blueTeamKills = playing.blueTeamKills
    )

    private fun createPlayingState(preparing: Preparing) =
        PlayingState(
            players = preparing.pendingPlayers.mapValues { (_, data) ->
                val dynamicState = createPlayerPlayingState(data.team)
                ShooterPlayerState(data, 0L, dynamicState)
            },
            gameDuration = preparing.gameDuration
        )

    private fun createPendingPlayerStateMap(vararg players: Long): Map<Long, ShooterPlayerData> = players
        .toList()
        .associateWithNotNull { createPendingPlayerData() }

    private fun createJoinedPlayerStateMap(
        vararg players: Long
    ): Map<Long, ShooterPlayerState> = players
        .toList()
        .associateWithNotNull { createJoinedPlayerState() }

    private fun createPendingPlayerData() = getPendingPlayerTeam()?.let(::ShooterPlayerData)

    private fun createJoinedPlayerState(): ShooterPlayerState? {
        val playerTeam = getJoinedPlayerTeam() ?: return null
        val data = ShooterPlayerData(playerTeam)
        val dynamicState = createPlayerPlayingState(playerTeam)
        return ShooterPlayerState(data, 0L, dynamicState)
    }

    private fun getPendingPlayerTeam(): PlayerTeam? {
        val players = ShooterGameState.preparing.pendingPlayers.getOrNull(state)?.values ?: return null
        val teamsToPlayerCount = players
            .groupBy { player -> player.team }
            .mapValues { (_, teamPlayers) -> teamPlayers.size }
        return teamsToPlayerCount
            .minByOrNull { (_, playerCount) -> playerCount }
            ?.key
            ?: Red
    }

    private fun getJoinedPlayerTeam(): PlayerTeam? {
        val players = ShooterGameState.playingState.players.getOrNull(state)?.values ?: return null
        val teamsToPlayerCount = players
            .groupBy { player -> player.data.team }
            .mapValues { (_, teamPlayers) -> teamPlayers.size }
        return teamsToPlayerCount
            .minByOrNull { (_, playerCount) -> playerCount }
            ?.key
            ?: Red
    }

    private fun createPlayerPlayingState(team: PlayerTeam) = Playing(
        hp = 100,
        transform = spawnIterator.fetchNextSpawn(team),
        verticalLookAngle = 0f
    )

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