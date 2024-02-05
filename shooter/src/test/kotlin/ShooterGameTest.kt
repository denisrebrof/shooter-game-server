import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.ShooterGame
import com.denisrebrof.shooter.domain.ShooterGameSettings
import com.denisrebrof.shooter.domain.model.Finished
import com.denisrebrof.shooter.domain.model.PlayingState
import com.denisrebrof.shooter.domain.model.ShooterGameIntents
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit


val playerIds = listOf(1L, 2L)

class ShooterGameTest {
    @Test
    fun testGame() {
        val settings = ShooterGameSettings(
            respawnDelay = 1000L,
            prepareDelay = 1000L,
            gameDuration = 1000L,
            completeDelay = 1000L,
            botSettings = ShooterGameSettings.BotSettings(
                defaultWeaponId = 1L,
                fillWithBotsToParticipantsCount = 5,
            ),
        )
        val game = ShooterGame
            .create(playerIds, settings)
            .also(ShooterGame::start)
        game
            .stateFlow
            .ofType(PlayingState::class.java)
            .delay(100L, TimeUnit.MILLISECONDS)
            .map { ShooterGameIntents.Hit(1L, 1L, 120, Transform(0f, 0f, 0f, 0f), 2L) }
            .doOnNext { game.submit(it) }
            .blockingFirst()
        val finished = game.stateFlow.ofType(Finished::class.java).blockingFirst()
        assert(finished.finishedPlayers[1L]?.kills == 1)
        assert(finished.finishedBots.keys.all { it < 0 })
        assert(finished.finishedBots.size == 3)
        assert(finished.finishedPlayers[2L]?.death == 1)
    }

    @Test
    fun testBots() {
        val settings = ShooterGameSettings(
            respawnDelay = 1000L,
            prepareDelay = 1000L,
            gameDuration = 1000L,
            completeDelay = 1000L,
            botSettings = ShooterGameSettings.BotSettings(
                defaultWeaponId = 1L,
                fillWithBotsToParticipantsCount = 5,
            ),
        )
        val game = ShooterGame
            .create(playerIds, settings)
            .also(ShooterGame::start)
        var playing = game.stateFlow.ofType(PlayingState::class.java).blockingFirst()
        assert(playing.bots.size == 3)

        game.removePlayers(1L)
        playing = game.stateFlow.ofType(PlayingState::class.java).blockingFirst()
        assert(playing.bots.size == 4)

        game.addPlayers(1L, 3L)
        playing = game.stateFlow.ofType(PlayingState::class.java).blockingFirst()
        assert(playing.bots.size == 2)

        val finished = game.stateFlow.ofType(Finished::class.java).blockingFirst()
        assert(finished.finishedBots.keys.all { it < 0 })
        assert(finished.finishedBots.size == 2)
    }
}