package com.denisrebrof.springboottest.hideandseekgame.domain.core.model

import com.denisrebrof.springboottest.game.domain.model.Transform
import kotlin.reflect.safeCast

abstract class Player(
    open val character: Character,
    open val initialTransform: Transform,
) {
    abstract val transform: Transform
    abstract fun moveTo(pos: Transform): Boolean
}

class Seeker(
    override var character: Character,
    override var initialTransform: Transform,
    initialState: State = State.Waiting,
) : Player(character, initialTransform) {

    private var internalTransform: Transform = initialTransform

    override val transform: Transform
        get() = internalTransform

    var catched: Int = 0
        private set

    var layed: Int = 0
        private set

    var state: State = initialState
        private set

    val hiderIdOrNull
        get() = State.Handling::class.safeCast(state)?.hiderId

    override fun moveTo(pos: Transform): Boolean {
        if (state == State.Waiting)
            return false

        internalTransform = pos
        return true
    }

    fun release(): Boolean {
        if (state !is State.Handling)
            return false

        state = State.Searching
        return true
    }

    fun catch(hiderId: Long) {
        catched += 1
        state = State.Handling(hiderId)
    }

    fun layDown() {
        layed += 1
        state = State.Searching
    }

    fun startSearching(): Boolean {
        if (state !is State.Waiting)
            return false

        state = State.Searching
        return true
    }

    sealed class State {
        object Waiting : State()
        object Searching : State()
        data class Handling(val hiderId: Long) : State()
    }
}

abstract class HiderBase(
    override var character: Character,
    final override val initialTransform: Transform,
    initialState: State = State.Hiding,
    var beenCatched: Int = 0,
    var beenLayed: Int = 0,
) : Player(character, initialTransform) {

    abstract fun getCatcherTransform(catcherId: Long): Transform?

    abstract fun getSleepPlaceTransform(placeId: Long): Transform

    var state: State = initialState
        private set

    private var selfTransform = initialTransform

    val catcherIdOrNull
        get() = State.Handled::class.safeCast(state)?.catcherId

    val sleepPlaceIdOrNull
        get() = State.Sleeping::class.safeCast(state)?.placeId

    override val transform: Transform
        get() = when (state) {
            State.Hiding -> selfTransform
            is State.Handled -> (state as State.Handled).catcherId.let(::getCatcherTransform)
            is State.Sleeping -> (state as State.Sleeping).placeId.let(::getSleepPlaceTransform)
        } ?: selfTransform

    override fun moveTo(pos: Transform): Boolean {
        if (state != State.Hiding)
            return false

        selfTransform = pos
        return true
    }

    fun attach(catcherId: Long) {
        beenCatched += 1
        state = State.Handled(catcherId)
    }

    fun layDown(sleepPlaceId: Long) {
        beenLayed += 1
        state = State.Sleeping(sleepPlaceId)
    }

    fun release(pos: Transform): Boolean {
        if (state !is State.Handled)
            return false

        selfTransform = pos
        state = State.Hiding
        return true
    }

    fun awake(): Boolean {
        if (state !is State.Sleeping)
            return false

        selfTransform = (state as State.Sleeping).placeId.let(::getSleepPlaceTransform)
        state = State.Hiding
        return true
    }

    sealed class State {
        object Hiding : State()
        data class Sleeping(val placeId: Long) : State()
        data class Handled(val catcherId: Long) : State()
    }
}