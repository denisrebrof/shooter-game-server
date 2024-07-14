package com.denisrebrof.shooter.domain.game

import com.denisrebrof.games.MVIGameHandler
import com.denisrebrof.matches.domain.model.IParticipantsHandler
import com.denisrebrof.shooter.domain.game.delegates.*
import com.denisrebrof.shooter.domain.model.*
import com.denisrebrof.shooter.domain.model.PlayerTeam.Blue
import com.denisrebrof.shooter.domain.model.PlayerTeam.Red
import com.denisrebrof.utils.spread
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

    private val playerStateFactory = PlayerStateFactory(settings)

    private val syncBotsDelegate = SyncBotsDelegate(settings, playerStateFactory)

    private val addPlayersDelegate = AddPlayersDelegate(settings, playerStateFactory, syncBotsDelegate)

    private val removePlayersDelegate = RemovePlayersDelegate(settings, syncBotsDelegate)

    private val updateBotsDelegate = UpdateBotDelegate(settings)

    private val reviveDelegate = RevivePlayerDelegate(settings)

    private val stateTransitionsDelegate = StateTransitionsDelegate(settings, syncBotsDelegate)

    fun start() = stateFlow
        .ofType(Preparing::class.java)
        .firstElement()
        .delay(settings.prepareDelay, TimeUnit.MILLISECONDS)
        .map(stateTransitionsDelegate::createPlayingState)
        .doAfterSuccess(::setState)
        .flatMap {
            val playingStateHandler = createBotUpdatesHandler()
            return@flatMap Completable
                .timer(settings.gameDuration, TimeUnit.MILLISECONDS)
                .toSingle { state }
                .doFinally { playingStateHandler.dispose() }
                .toMaybe()
        }
        .ofType(PlayingState::class.java)
        .map(stateTransitionsDelegate::createFinishedState)
        .doAfterSuccess(::setState)
        .delay(settings.completeDelay, TimeUnit.MILLISECONDS)
        .subscribeDefault { send(Action.LifecycleCompleted) }
        .let(::add)

//    override fun setState(state: ShooterGameState) {
//        super.setState(state)
//        if(state !is PlayingState)
//            return
//
//        val st = state
//            .teamData
//            .entries
//            .firstNotNullOfOrNull { (team, data) -> data.flagPlayerId?.let { team to it } }
//            ?: return println("Not Found playerFlag")
//        println("Found playerFlag: ${st.first} - ${st.second}")
//    }

    override fun onIntentReceived(intent: Intent) = when (intent) {
        is Intent.SelectWeapon -> mutateState(PlayingState::class) { selectWeapon(intent.playerId, intent.weaponId) }
        is Intent.HitByBot -> hitByBot(intent)
        is Intent.TakeFlag -> mutateState(PlayingState::class) { takeFlag(intent.playerId) }
        is Intent.StoreFlag -> mutateState(PlayingState::class) { storeFlag(intent.playerId) }
        is Intent.ReturnFlag -> mutateState(PlayingState::class) { returnFlag(intent.playerId, settings) }
        is Intent.Hit -> hit(intent)
        is Intent.UpdatePos -> mutateState(PlayingState::class) {
            updatePlayerPos(intent.playerId, intent.pos, intent.verticalLookAngle, intent.crouching, intent.jumping)
        }

        is Intent.Shoot -> updateStateCopy(PlayingState::class) { current ->
            val result = shoot(current, intent.shooterId, intent.weaponId)
            if (!result)
                return@updateStateCopy

            Action.Shoot(intent.shooterId, intent.weaponId, intent.direction).let(::send)
        }

        is Intent.SubmitBotsVisibility -> withState(PlayingState::class) {
            val currentHash = participantIds.hashCode()
            if (currentHash != intent.playersHash)
                return@withState

            intent
                .targets
                .entries
                .forEach { it.spread(updateBotsDelegate::setTarget) }
        }
    }

    override fun addPlayers(vararg players: Long) {
        updateState(PlayingState::class) { addPlayersDelegate.addPlayers(it, *players) }
        updateState(Preparing::class) { addPlayersDelegate.addPlayers(it, *players) }

        players
            .map { Action.JoinedStateChange(it, true) }
            .forEach(::send)
    }

    override fun removePlayers(vararg players: Long) {
        updateState(PlayingState::class) { removePlayersDelegate.removePlayers(it, *players) }
        updateState(Preparing::class) { removePlayersDelegate.removePlayers(it, *players) }

        players
            .map { Action.JoinedStateChange(it, false) }
            .forEach(::send)
    }

    private fun hitByBot(intent: Intent.HitByBot) {
        val hitIntent = Intent.Hit(
            shooterId = intent.shooterId,
            receiverId = intent.receiverId,
            damage = 10
        )
        hit(hitIntent)
    }

    private fun hit(intent: Intent.Hit) = withState(PlayingState::class) {
        val hitResult = with(intent) { hit(shooterId, receiverId, damage) }
        setState(hitResult.state)

        val action = with(hitResult) { intent.toAction(killed, hpLoss) }
        send(action)

        if (!hitResult.killed)
            return@withState

        createReviveHandler(intent.receiverId)
    }

    private fun createReviveHandler(playerId: Long) = Completable
        .timer(settings.respawnDelay, TimeUnit.MILLISECONDS)
        .doOnComplete { revivePlayer(playerId) }
        .subscribeDefault()
        .let(::add)

    private fun revivePlayer(playerId: Long) = updateState(PlayingState::class) {
        reviveDelegate.revivePlayer(it, playerId)
    }

    private fun createBotUpdatesHandler(): Disposable = handleBotUpdates()

    private fun handleBotUpdates() = Flowable
        .interval(settings.botSettings.botUpdateLoopDelayMs, TimeUnit.MILLISECONDS)
        .onBackpressureDrop()
        .subscribeDefault {
            withState(PlayingState::class) {
                val result = updateBotsDelegate.updateBots(this)
                setState(result.state)
                result.actions.forEach(::send)
                result.hits.forEach(::hit)
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