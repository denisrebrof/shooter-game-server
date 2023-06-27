package com.denisrebrof.springboottest.hideandseekgame

import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.hideandseekgame.domain.core.GameSettings
import com.denisrebrof.springboottest.hideandseekgame.domain.core.GameState
import com.denisrebrof.springboottest.hideandseekgame.domain.core.HNSGame
import com.denisrebrof.springboottest.hideandseekgame.domain.core.PlayerInput
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.Character
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.Role
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.RoundEvent
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

private val defaultTransform = Transform(0f, 0f, 0f, 0f)
private val defaultSettings = GameSettings(
    roles = listOf(
        Role(Character.Hider1, defaultTransform, false),
        Role(Character.Seeker1, defaultTransform.copy(x = 0.5f), true)
    ),
    sleepPlaces = mapOf()
)

private val defaultUsers = setOf(100L, 200L)

class HNSGameTests {
    @Test
    fun testGameLoopPassesCorrectly() {
        val game = HNSGame(defaultUsers, defaultSettings)
        game.stateFlow.subscribe(::println)
        game.getEvents().subscribe { println("round event: $it") }
        game.start()
        game.stateFlow.filter(GameState.Finished::equals).blockingFirst()
        assert(true)
    }

    @Test
    fun testRoundEvents() {
        val game = HNSGame(defaultUsers, defaultSettings)
        game.stateFlow.subscribe { println("Game State: $it") }
        game.getEvents().subscribe { println("round event: $it") }
        game.start()
        game.stateFlow.filter(GameState.Finished::equals).blockingFirst()
        assert(true)
    }

    @Test
    fun testCatchingWorks() {
        val game = HNSGame(defaultUsers, defaultSettings)
        game.stateFlow.subscribe { println("Game State: $it") }
        game.getEvents().subscribe { println("Round event: $it") }
        game.start()
        val firstEventAfterCatched = game
            .stateFlow
            .filter(GameState.Searching::equals)
            .delay(100L, TimeUnit.MILLISECONDS)
            .firstElement()
            .doOnSuccess { game.submitInput(PlayerInput.Catch(100, 200)) }
            .flatMap { game.getEvents().firstElement() }
            .blockingGet()

        val nextUpdate = firstEventAfterCatched as? RoundEvent.Update
        assert(nextUpdate != null)

        val snapshot = nextUpdate?.snapshot ?: return
        val hider = snapshot.hiders.values.first()
        val seeker = snapshot.seekers.values.first()
        assert(hider.beenCatched == 1 && hider.catcherId != null)
        assert(seeker.catched == 1 && seeker.catchedHiderId != null)
        assert(hider.transform == seeker.transform)
    }
}
