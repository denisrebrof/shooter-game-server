package com.denisrebrof.springboottest.hideandseekgame

import com.denisrebrof.springboottest.hideandseekgame.core.Character
import com.denisrebrof.springboottest.hideandseekgame.core.Role
import com.denisrebrof.springboottest.hideandseekgame.core.Transform
import com.denisrebrof.springboottest.user.domain.model.User
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

private val defaultTransform = Transform(0f, 0f, 0f, 0f)
private val defaultSettings = GameSettings(
    roles = listOf(
        Role(Character.Hider1, defaultTransform, false),
        Role(Character.Seeker1, defaultTransform, true)
    ),
    sleepPlaces = mapOf()
)
val user1 = User(id = 100)
val user2 = User(id = 200)

private val defaultUsers = listOf(user1, user2)

class GameTests {
    @Test
    fun testGameLoopPassesCorrectly() {
        val game = Game(defaultUsers, defaultSettings)
        game.stateFlow.subscribe { println(it) }
        game.start()
        game.stateFlow.filter(GameState.Finished::equals).blockingFirst()
        assert(true)
    }

    @Test
    fun testRoundEvents() {
        val game = Game(defaultUsers, defaultSettings)
        game.stateFlow.subscribe { println("Game State: $it") }
        game.getRoundEvents().subscribe { println("round event: $it") }
        game.start()
        game.stateFlow.filter(GameState.Finished::equals).blockingFirst()
        assert(true)
    }

    @Test
    fun testCatchingFinishesGame() {
        val game = Game(defaultUsers, defaultSettings)
        game.stateFlow.subscribe { println("Game State: $it") }
        game.getRoundEvents().subscribe { println("round event: $it") }
        game
            .stateFlow
            .filter(GameState.Searching::equals)
            .delay(100L, TimeUnit.MILLISECONDS)
            .subscribe { game.submitInput(PlayerInput.Catch(100, 200)) }
        game.start()
        game.stateFlow.filter(GameState.Finished::equals).blockingFirst()
        assert(true)
    }
}
