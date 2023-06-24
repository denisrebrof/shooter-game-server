package com.denisrebrof.springboottest.hideandseekgame.domain.core

import com.denisrebrof.springboottest.game.domain.RoundBase
import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import kotlin.reflect.safeCast

class HNSRound(
    playerRoles: Map<Long, Role>,
    private val sleepPlaces: Map<Long, SleepPlace>,
    private val settings: HNSRoundSettings,
    durationMs: Long,
) : RoundBase(settings.updateIntervalMs, durationMs) {

    private val eventsProcessor = PublishProcessor.create<RoundEvent>()

    private val seekers = playerRoles
        .filterValues(Role::isSeeker)
        .mapValues { (_, role) -> createSeeker(role) }
        .toMutableMap()

    private val hiders = playerRoles
        .filterValues(Role::isHider)
        .mapValues { (_, role) -> createHider(role) }
        .toMutableMap()

    private val statsSnapshot
        get() = RoundSnapshot(
            seekers.mapValues { (_, seeker) -> seeker.snapshot },
            hiders.mapValues { (_, hider) -> hider.snapshot }
        )

    private val Seeker.snapshot
        get() = SeekerSnapshotItem(
            transform = transform,
            character = character,
            catched = catched,
            layed = layed,
            catchedHiderId = Seeker.State.Handling::class.safeCast(state)?.hiderId
        )

    private val HiderBase.snapshot
        get() = HiderSnapshotItem(
            transform = transform,
            character = character,
            beenCatched = beenCatched,
            beenLayed = beenLayed,
            catcherId = HiderBase.State.Handled::class.safeCast(state)?.catcherId,
            sleepPlaceId = HiderBase.State.Sleeping::class.safeCast(state)?.placeId,
        )

    val events: Flowable<RoundEvent> = eventsProcessor

    private fun createSeeker(role: Role) = Seeker(role.character, role.initialPos)

    private fun createHider(role: Role) = Hider(role.character, role.initialPos)

    private fun finishGame(reason: HNSRoundFinishReason) = finishGame {
        RoundEvent.Finished(reason, statsSnapshot).let(eventsProcessor::onNext)
    }

    override fun onTimeLeft() {
        super.onTimeLeft()
        RoundEvent.Finished(HNSRoundFinishReason.TimeLeft, statsSnapshot).let(eventsProcessor::onNext)
    }

    override fun update(timeLeftMs: Long) {
        RoundEvent.Update(timeLeftMs, statsSnapshot).let(eventsProcessor::onNext)
    }

    private fun checkAllHidersSleeping() {
        val allSleeping = hiders.values.all { it.state is HiderBase.State.Sleeping }
        if (!allSleeping)
            return

        finishGame(reason = HNSRoundFinishReason.AllHidersSleeping)
    }

    private fun removeSeeker(seeker: Seeker) = seeker.run {
        hiderIdOrNull?.let(hiders::get)?.release(transform)
    }

    private fun removeHider(hider: Hider) = hider.run {
        catcherIdOrNull?.let(seekers::get)?.release()
        sleepPlaceIdOrNull?.let(sleepPlaces::get)?.release()
    }

    fun startSearching() {
        if (isFinished)
            return

        seekers.forEach { (_, seeker) -> seeker.startSearching() }
    }

    fun stop() = finishGame(HNSRoundFinishReason.Aborted)

    fun tryMove(playerId: Long, pos: Transform) {
        if (isFinished)
            return

        val player = hiders[playerId] ?: seekers[playerId] ?: return
        player.moveTo(pos)
    }

    fun tryCatch(hiderId: Long, catcherId: Long) {
        if (isFinished)
            return

        val hider = hiders[hiderId] ?: return
        val catcher = seekers[catcherId] ?: return
        if (hider.state != HiderBase.State.Hiding || catcher.state != Seeker.State.Searching)
            return

        if (!hider.transform.isClose(catcher.transform, settings.catchDistance))
            return

        hider.attach(catcherId)
        catcher.catch(hiderId)
    }

    fun tryLay(hiderId: Long, catcherId: Long, placeId: Long) {
        if (isFinished)
            return

        val hider = hiders[hiderId] ?: return
        val catcher = seekers[catcherId] ?: return
        val place = sleepPlaces[catcherId] ?: return
        if (hider.state !is HiderBase.State.Handled || catcher.state !is Seeker.State.Handling || place.isOccupied)
            return

        if (!place.transform.isClose(catcher.transform, settings.layDistance))
            return

        hider.layDown(placeId)
        catcher.layDown()
        place
            .layDown(hiderId) { hider.awake() }
            .let(gameUpdateDisposables::add)

        checkAllHidersSleeping()
    }

    fun removePlayer(userId: Long) {
        seekers.remove(userId)?.let(::removeSeeker)
        hiders.remove(userId)?.let(::removeHider)
        if (seekers.isNotEmpty() && hiders.isNotEmpty())
            return

        finishGame(HNSRoundFinishReason.UsersLeft)
    }

    inner class Hider(character: Character, initialPos: Transform) : HiderBase(character, initialPos) {
        override fun getCatcherTransform(catcherId: Long) = seekers[catcherId]?.transform
        override fun getSleepPlaceTransform(placeId: Long) = sleepPlaces.getValue(placeId).transform
    }
}

data class HNSRoundSettings(
    val updateIntervalMs: Long = 100L,
    val catchDistance: Float = 1f,
    val layDistance: Float = 1f
)

enum class HNSRoundFinishReason {
    UsersLeft,
    Aborted,
    TimeLeft,
    AllHidersSleeping,
}