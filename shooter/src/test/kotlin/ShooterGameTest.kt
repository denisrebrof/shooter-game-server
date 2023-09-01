import gameentities.Transform
import model.Finished
import model.Playing
import model.PlayingState
import model.ShooterGameIntents
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit


val playerIds = listOf(1L, 2L)

class ShooterGameTest {
    @Test
    fun testGame() {
        val settings = ShooterGameSettings(prepareDelay = 1000L, gameDuration = 1000L)
        val game = ShooterGame.create(playerIds, settings)
        game
            .stateFlow
            .ofType(PlayingState::class.java)
            .delay(100L, TimeUnit.MILLISECONDS)
            .map { ShooterGameIntents.Shoot(1L, 1L, 1000, Transform(0f,0f,0f,0f), 2L) }
            .doOnNext { game.submit(it) }
            .blockingFirst()
        val finished = game.stateFlow.ofType(Finished::class.java).blockingFirst()
        assert(finished.finishedPlayers[1L]?.kills == 1)
        assert(finished.finishedPlayers[2L]?.death == 1)
        assert(true)
    }
}