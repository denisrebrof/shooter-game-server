package com.denisrebrof.games

import arrow.optics.copy
import arrow.optics.dsl.index
import arrow.optics.optics
import arrow.optics.typeclasses.Index
import com.denisrebrof.utils.subscribeDefault
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class GameHandlerTests {
    sealed class SampleGameState(open val playerIds: Set<Long>) {
        data class Preparing(override val playerIds: Set<Long>) : SampleGameState(playerIds)

        @optics
        data class Playing(
            val playerToScore: Map<Long, Int>,
            val test: TestDataOne
        ) : SampleGameState(playerToScore.keys) {
            companion object
        }

        @optics
        data class TestDataOne(val data: TestDataTwo) {
            companion object
        }

        @optics
        data class TestDataTwo(val data: String) {
            companion object
        }

        data class Finished(
            override val playerIds: Set<Long>,
            val winnerId: Long,
            val winnerScore: Int
        ) : SampleGameState(playerIds)
    }

    private enum class SampleGameAction {
        Fireworks
    }

    private data class SampleGameAddScoreIntent(val playerId: Long)

    private class SampleGame(
        playerIds: Set<Long>
    ) : MVIGameHandler<SampleGameState, SampleGameAddScoreIntent, SampleGameAction>(SampleGameState.Preparing(playerIds)) {

        fun start() = Maybe
            .timer(1000L, TimeUnit.MILLISECONDS)
            .map { state }
            .ofType(SampleGameState.Preparing::class.java)
            .map {
                SampleGameState.Playing(
                    it.playerIds.associateWith { 0 },
                    SampleGameState.TestDataOne(SampleGameState.TestDataTwo("test"))
                )
            }
            .subscribeDefault(::setState)
            .also(::add)

        override fun onIntentReceived(intent: SampleGameAddScoreIntent) {
            val playingState = state as? SampleGameState.Playing ?: return
            val currentScore = playingState.playerToScore[intent.playerId] ?: return
            val newScore = currentScore + 1
            if (newScore >= 5)
                return SampleGameState.Finished(state.playerIds, winnerId = intent.playerId, winnerScore = newScore)
                    .let(::setState)

            send(SampleGameAction.Fireworks)
            return playingState.copy {
                SampleGameState.Playing.test.data.data transform { "$$it$" }
                SampleGameState.Playing.playerToScore.index(Index.map(), intent.playerId) transform { newScore }
            }.let(::setState)
        }
    }

    @Test
    fun testSampleGame() {
        val playerIds = setOf(0L, 1L)
        val gameHandler = SampleGame(playerIds).also(SampleGame::start)

        gameHandler
            .stateFlow
            .ofType(SampleGameState.Playing::class.java)
            .firstElement()
            .blockingSubscribe()

        val playingStateSubscription = Flowable
            .interval(100L, TimeUnit.MILLISECONDS)
            .map { SampleGameAddScoreIntent(0L) }
            .subscribe(gameHandler::submit)
            .also(gameHandler::add)

        val finishedState = gameHandler
            .stateFlow
            .ofType(SampleGameState.Finished::class.java)
            .firstElement()
            .blockingGet()!!

        assert(finishedState.winnerId == 0L)

        gameHandler.dispose()

        assert(playingStateSubscription.isDisposed)
    }
}