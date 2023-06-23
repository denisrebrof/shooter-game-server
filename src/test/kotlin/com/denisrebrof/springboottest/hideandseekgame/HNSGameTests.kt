package com.denisrebrof.springboottest.hideandseekgame

import com.denisrebrof.springboottest.hideandseekgame.model.Character
import com.denisrebrof.springboottest.hideandseekgame.model.Role
import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.hideandseekgame.model.RoundEvent
import com.denisrebrof.springboottest.user.domain.model.User
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
val user1 = User(id = 100)
val user2 = User(id = 200)

private val defaultUsers = listOf(user1, user2)

class HNSGameTests {
    @Test
    fun testGameLoopPassesCorrectly() {
        val game = HNSGame(defaultUsers, defaultSettings)
        game.stateFlow.subscribe { println(it) }
        game.start()
        game.stateFlow.filter(GameState.Finished::equals).blockingFirst()
        assert(true)
    }

    @Test
    fun testRoundEvents() {
        val game = HNSGame(defaultUsers, defaultSettings)
        game.stateFlow.subscribe { println("Game State: $it") }
        game.getRoundEvents().subscribe { println("round event: $it") }
        game.start()
        game.stateFlow.filter(GameState.Finished::equals).blockingFirst()
        assert(true)
    }

    @Test
    fun testCatchingWorks() {
        val game = HNSGame(defaultUsers, defaultSettings)
        game.stateFlow.subscribe { println("Game State: $it") }
        game.getRoundEvents().subscribe { println("round event: $it") }
        game.start()
        val firstEventAfterCatched = game
            .stateFlow
            .filter(GameState.Searching::equals)
            .delay(100L, TimeUnit.MILLISECONDS)
            .firstElement()
            .doOnSuccess { game.submitInput(PlayerInput.Catch(100, 200)) }
            .flatMap { game.getRoundEvents().firstElement() }
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
