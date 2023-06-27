package com.denisrebrof.springboottest.hideandseekgame.domain.core

import com.denisrebrof.springboottest.game.domain.GameBase
import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.Role
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.RoundEvent
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.SleepPlace
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.processors.BehaviorProcessor
import java.util.concurrent.TimeUnit

class HNSGame(
    userIds: Set<Long>,
    private val settings: GameSettings,
) : GameBase<GameState, PlayerInput, RoundEvent>(userIds, GameState.Pending) {

    private val roundProcessor = BehaviorProcessor.create<HNSRound>()

    private val round: HNSRound?
        get() = roundProcessor.value

    override fun createGameLifecycle(): Completable = Maybe
        .timer(settings.pendingDurationMs, TimeUnit.MILLISECONDS)
        .thenGoToState(GameState.SettingRoles, ::setUpRoles)
        .thenGoToState(GameState.Hiding, ::setUpHiding)
        .thenGoToState(GameState.Searching, ::setUpSearching)
        .thenGoToState(GameState.Finished, ::setUpFinish)

    override fun onStop() = round?.stop() ?: Unit

    override fun submitInput(input: PlayerInput) = round?.sendInput(input) ?: Unit

    override fun onRemovePlayer(userId: Long) = round?.removePlayer(userId) ?: Unit

    override fun getEvents(): Flowable<RoundEvent> = roundProcessor.switchMap(HNSRound::events)

    private fun setUpRoles() = Maybe
        .just(assignRoles())
        .delay(settings.settingRolesDurationMs, TimeUnit.MILLISECONDS)

    private fun setUpHiding(playerRoles: Map<Long, Role>): Maybe<HNSRound> {
        val round = startRound(playerRoles)
        return Maybe
            .timer(settings.hidingDurationMs, TimeUnit.MILLISECONDS)
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
    ).also(roundProcessor::onNext).also(HNSRound::start)

    private fun HNSRound.sendInput(input: PlayerInput) = when (input) {
        is PlayerInput.Catch -> tryCatch(input.targetId, input.playerId)
        is PlayerInput.Movement -> tryMove(input.playerId, input.pos)
        is PlayerInput.Lay -> tryLay(input.playerId, input.playerId, input.placeId)
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
