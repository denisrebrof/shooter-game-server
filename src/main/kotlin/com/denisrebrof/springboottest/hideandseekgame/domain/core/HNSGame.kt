package com.denisrebrof.springboottest.hideandseekgame.domain.core

import com.denisrebrof.springboottest.game.domain.GameBase
import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.Role
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.RoundEvent
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.SleepPlace
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.processors.PublishProcessor
import java.util.concurrent.TimeUnit

class HNSGame(
    userIds: Set<Long>,
    private val settings: GameSettings,
) : GameBase<GameState>(GameState.Pending) {

    private val players = userIds.toMutableSet()

    private val inputProcessor = PublishProcessor.create<PlayerInput>()
    private val roundEventsProcessor = PublishProcessor.create<RoundEvent>()
    private val removedPlayersProcessor = PublishProcessor.create<Long>()
    private val stopEventProcessor = PublishProcessor.create<Unit>()

    override fun createGameLifecycle(): Completable = Maybe
        .timer(settings.pendingDurationMs, TimeUnit.MILLISECONDS)
        .thenGoToState(GameState.SettingRoles, ::setUpRoles)
        .thenGoToState(GameState.Hiding, ::setUpHiding)
        .thenGoToState(GameState.Searching, ::setUpSearching)
        .thenGoToState(GameState.Finished, ::setUpFinish)

    override fun onStop() = stopEventProcessor.onNext(Unit)

    fun submitInput(input: PlayerInput) = inputProcessor.onNext(input)

    fun getRoundEvents(): Flowable<RoundEvent> = roundEventsProcessor

    fun removePlayer(userId: Long) = userId
        .also(players::remove)
        .let(removedPlayersProcessor::onNext)

    private fun setUpRoles() = Maybe
        .just(assignRoles())
        .delay(settings.settingRolesDurationMs, TimeUnit.MILLISECONDS)

    private fun setUpHiding(playerRoles: Map<Long, Role>): Maybe<HNSRound> {
        val round = startRound(playerRoles)

        val goToSearchingTimer = Maybe
            .timer(settings.hidingDurationMs, TimeUnit.MILLISECONDS)
            .map { GameState.Searching }

        val finishedHandler = round
            .waitUntilFinished()
            .map { GameState.Finished }

        return Maybe

            .map { round }
    }

    private fun setUpSearching(round: HNSRound): Maybe<RoundEvent.Finished> = round
        .also(HNSRound::startSearching)
        .events
        .ofType(RoundEvent.Finished::class.java)
        .firstElement()

    private fun setUpFinish(finishedEvent: RoundEvent.Finished): Completable {
        //TODO check if need do something else
        return Completable.timer(settings.finishDurationMs, TimeUnit.MILLISECONDS)
    }

    private fun assignRoles(): Map<Long, Role> {
        val randomRoles = settings.roles.shuffled()
        val seekers = randomRoles.filter(Role::isSeeker).iterator()
        val hiders = randomRoles.filterNot(Role::isSeeker).iterator()
        return players
            .mapIndexed { index, userId -> userId to (index % 2 == 0) }
            .associate { (userId, isSeeker) -> userId to if (isSeeker) seekers.next() else hiders.next() }
    }

    private fun startRound(playerRoles: Map<Long, Role>) = HNSRound(
        playerRoles = playerRoles,
        sleepPlaces = settings.sleepPlaces,
        settings = settings.roundSettings,
        durationMs = settings.gameDurationMs,
    )
        .also(::setupInput)
        .also(::setupStateOutput)
        .also(::setupPlayersRemove)
        .also(::setupStop)
        .also(HNSRound::start)

    private fun HNSRound.sendInput(
        input: PlayerInput
    ) = when (input) {
        is PlayerInput.Catch -> tryCatch(input.targetId, input.playerId)
        is PlayerInput.Movement -> tryMove(input.playerId, input.pos)
        is PlayerInput.Lay -> tryLay(input.playerId, input.playerId, input.placeId)
    }

    private fun HNSRound.waitUntilFinished() = events
        .ofType(RoundEvent.Finished::class.java)
        .firstElement()

    private fun setupStateOutput(round: HNSRound) = round
        .events
        .subscribeDefault(roundEventsProcessor::onNext)
        .let(lifecycle::add)

    private fun setupPlayersRemove(round: HNSRound) = removedPlayersProcessor
        .subscribeDefault(round::removePlayer)
        .let(lifecycle::add)

    private fun setupStop(round: HNSRound) = stopEventProcessor
        .subscribeDefault { round.stop() }
        .let(lifecycle::add)

    private fun setupInput(round: HNSRound) {
        val inputHandler = inputProcessor
            .doOnNext { input -> round.sendInput(input) }
            .ignoreElements()
        round
            .events
            .ofType(RoundEvent.Finished::class.java)
            .firstElement()
            .ignoreElement()
            .ambWith(inputHandler)
            .subscribeDefault()
            .let(lifecycle::add)
    }
}

data class GameSettings(
    val roles: List<Role>,
    val sleepPlaces: Map<Long, SleepPlace>,
    val roundSettings: HNSRoundSettings = HNSRoundSettings(),
    val pendingDurationMs: Long = 3000L,
    val settingRolesDurationMs: Long = 3000L,
    val hidingDurationMs: Long = 3000L,
    val searchingDurationMs: Long = 3000L,
    val finishDurationMs: Long = 3000L,
) {
    val gameDurationMs = hidingDurationMs + searchingDurationMs
}

sealed class PlayerInput(open val playerId: Long) {
    data class Movement(
        override val playerId: Long,
        val pos: Transform
    ) : PlayerInput(playerId)

    data class Catch(
        override val playerId: Long,
        val targetId: Long
    ) : PlayerInput(playerId)

    data class Lay(
        override val playerId: Long,
        val targetId: Long,
        val placeId: Long,
    ) : PlayerInput(playerId)
}

enum class GameState(val code: Long) {
    Pending(1),
    SettingRoles(2),
    Hiding(3),
    Searching(4),
    Finished(5)
}
