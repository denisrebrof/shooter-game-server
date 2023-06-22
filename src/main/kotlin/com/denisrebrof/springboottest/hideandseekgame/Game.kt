package com.denisrebrof.springboottest.hideandseekgame

import com.denisrebrof.springboottest.hideandseekgame.core.Role
import com.denisrebrof.springboottest.hideandseekgame.core.SleepPlace
import com.denisrebrof.springboottest.hideandseekgame.core.Transform
import com.denisrebrof.springboottest.hideandseekgame.round.HideAndSeekRound
import com.denisrebrof.springboottest.hideandseekgame.round.HideAndSeekRoundSettings
import com.denisrebrof.springboottest.hideandseekgame.round.RoundEvent
import com.denisrebrof.springboottest.user.domain.model.User
import com.denisrebrof.springboottest.utils.subscribeOnIO
import com.denisrebrof.springboottest.utils.subscribeWithLogError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.processors.PublishProcessor
import java.util.concurrent.TimeUnit

class Game(
    private val users: List<User>,
    private val settings: GameSettings,
) : GameBase<GameState>(GameState.Pending) {

    private val inputProcessor = PublishProcessor.create<PlayerInput>()
    private val roundEventsProcessor = PublishProcessor.create<RoundEvent>()

    private fun assignRoles(): Map<Long, Role> {
        val randomRoles = settings.roles.shuffled()
        val seekers = randomRoles.filter(Role::isSeeker).iterator()
        val hiders = randomRoles.filterNot(Role::isSeeker).iterator()
        return users
            .mapIndexed { index, user -> user.id to (index % 2 == 0) }
            .associate { (userId, isSeeker) -> userId to if (isSeeker) seekers.next() else hiders.next() }
    }

    private fun setUpRoles() = Maybe
        .just(assignRoles())
        .delay(settings.settingRolesDurationMs, TimeUnit.MILLISECONDS)

    private fun createRound(playerRoles: Map<Long, Role>) = HideAndSeekRound(
        playerRoles = playerRoles,
        sleepPlaces = settings.sleepPlaces,
        durationMs = settings.gameDurationMs,
        settings = settings.roundSettings,
    )
        .also(HideAndSeekRound::start)
        .also(::setupStateOutput)

    private fun setUpHiding(playerRoles: Map<Long, Role>): Maybe<HideAndSeekRound> {
        val round = createRound(playerRoles).also(::setupInput)
        return Maybe
            .timer(settings.hidingDurationMs, TimeUnit.MILLISECONDS)
            .map { round }
    }

    private fun HideAndSeekRound.sendInput(
        input: PlayerInput
    ) = when (input) {
        is PlayerInput.Catch -> tryCatch(input.targetId, input.playerId)
        is PlayerInput.Movement -> tryMove(input.playerId, input.pos)
        is PlayerInput.Lay -> tryLay(input.playerId, input.playerId, input.placeId)
    }

    private fun setupStateOutput(round: HideAndSeekRound) = round
        .events
        .subscribeOnIO()
        .subscribeWithLogError(roundEventsProcessor::onNext)
        .let(gameLifecycle::add)

    private fun setupInput(round: HideAndSeekRound) {
        val inputHandler = inputProcessor
            .doOnNext { input -> round.sendInput(input) }
            .ignoreElements()
        round
            .events
            .ofType(RoundEvent.Finished::class.java)
            .firstElement()
            .ignoreElement()
            .ambWith(inputHandler)
            .subscribeOnIO()
            .subscribeWithLogError()
            .let(gameLifecycle::add)
    }

    private fun setUpSearching(round: HideAndSeekRound): Maybe<RoundEvent.Finished> = round
        .also(HideAndSeekRound::startSearching)
        .events
        .ofType(RoundEvent.Finished::class.java)
        .firstElement()

    private fun setUpFinish(finishedEvent: RoundEvent.Finished): Completable {
        //TODO check if need do something else
        return Completable.timer(settings.finishDurationMs, TimeUnit.MILLISECONDS)
    }

    override fun createGameLoop(): Completable = Maybe
        .timer(settings.pendingDurationMs, TimeUnit.MILLISECONDS)
        .thenGoToState(GameState.SettingRoles, ::setUpRoles)
        .thenGoToState(GameState.Hiding, ::setUpHiding)
        .thenGoToState(GameState.Searching, ::setUpSearching)
        .thenGoToState(GameState.Finished, ::setUpFinish)

    fun submitInput(input: PlayerInput) = inputProcessor.onNext(input)

    fun getRoundEvents(): Flowable<RoundEvent> = roundEventsProcessor
}

data class GameSettings(
    val roles: List<Role>,
    val sleepPlaces: Map<Long, SleepPlace>,
    val roundSettings: HideAndSeekRoundSettings = HideAndSeekRoundSettings(),
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

enum class GameState {
    Pending,
    SettingRoles,
    Hiding,
    Searching,
    Finished
}
