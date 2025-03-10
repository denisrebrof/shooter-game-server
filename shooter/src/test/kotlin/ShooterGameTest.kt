import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.game.ShooterGame
import com.denisrebrof.shooter.domain.model.ShooterGameSettings
import com.denisrebrof.shooter.domain.model.Finished
import com.denisrebrof.shooter.domain.model.PlayingState
import com.denisrebrof.shooter.domain.model.ShooterGameIntents
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit


val playerIds = listOf(1L, 2L)

class ShooterGameTest {

    private val defaultMap = ShooterGameSettings.MapSettings(
        redTeamSpawnPos = listOf(Transform.Zero),
        blueTeamSpawnPos =  listOf(Transform.Zero),
        redTeamFlagPos = Transform.Zero,
        blueTeamFlagPos = Transform.Zero,
        redTeamRoutes =  listOf(listOf(Transform.Zero)),
        blueTeamRoutes =  listOf(listOf(Transform.Zero))
    )

    @Test
    fun testGame() {
        val settings = ShooterGameSettings(
            defaultHp = 100,
            respawnDelay = 1000L,
            prepareDelay = 1000L,
            gameDuration = 1000L,
            completeDelay = 1000L,
            botSettings = ShooterGameSettings.BotSettings(
                defaultWeaponId = 1L,
                fillWithBotsToTeamSize = 3,
            ),
            mapSettings = defaultMap
        )
        val game = ShooterGame
            .create(playerIds, settings)
            .also(ShooterGame::start)
        game
            .stateFlow
            .ofType(PlayingState::class.java)
            .delay(100L, TimeUnit.MILLISECONDS)
            .map { ShooterGameIntents.Hit(1L, 2L, 120) }
            .doOnNext(game::submit)
            .blockingFirst()

        val finished = game.stateFlow.ofType(Finished::class.java).blockingFirst()
        assert(finished.finishedPlayers[1L]?.kills == 1)
        assert(finished.finishedPlayers[2L]?.death == 1)
        assert(finished.finishedBots.keys.all { it < 0 })
        assert(finished.finishedBots.size == 4)
    }

    @Test
    fun testBots() {
        val settings = ShooterGameSettings(
            defaultHp = 100,
            respawnDelay = 1000L,
            prepareDelay = 1000L,
            gameDuration = 1000L,
            completeDelay = 1000L,
            botSettings = ShooterGameSettings.BotSettings(
                defaultWeaponId = 1L,
                fillWithBotsToTeamSize = 3,

            ),
            mapSettings = defaultMap
        )
        val game = ShooterGame
            .create(playerIds, settings)
            .also(ShooterGame::start)
        var playing = game.stateFlow.ofType(PlayingState::class.java).blockingFirst()
        assert(playing.bots.size == 4)

        game.removePlayers(1L)
        playing = game.stateFlow.ofType(PlayingState::class.java).blockingFirst()
        assert(playing.bots.size == 5)

        game.addPlayers(1L, 3L)
        playing = game.stateFlow.ofType(PlayingState::class.java).blockingFirst()
        assert(playing.bots.size == 3)

        val finished = game.stateFlow.ofType(Finished::class.java).blockingFirst()
        assert(finished.finishedBots.keys.all { it < 0 })
        assert(finished.finishedBots.size == 3)
    }
}