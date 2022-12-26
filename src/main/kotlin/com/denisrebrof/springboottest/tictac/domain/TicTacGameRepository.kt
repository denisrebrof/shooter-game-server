package com.denisrebrof.springboottest.tictac.domain

import com.denisrebrof.springboottest.tictac.domain.model.TicTacGame
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.stereotype.Service
import java.util.*

@Service
class TicTacGameRepository {
    private val matchIdToGame = Collections.synchronizedMap(mutableMapOf<String, TicTacGame>())

    private val updates = PublishProcessor.create<GameUpdate>()

    fun set(matchId: String, game: TicTacGame) {
        val existingGame = matchIdToGame.containsKey(matchId)
        val updateType = when {
            existingGame -> GameUpdateType.Updated
            else -> GameUpdateType.Created
        }
        matchIdToGame[matchId] = game
        GameUpdate(matchId, game, updateType).let(updates::onNext)
    }

    fun remove(matchId: String) {
        val game = matchIdToGame.remove(matchId) ?: return
        GameUpdate(matchId, game, GameUpdateType.Created).let(updates::onNext)
    }

    fun get(matchId: String): TicTacGame? = matchIdToGame[matchId]

    fun getUpdates(): Flowable<GameUpdate> = updates

    data class GameUpdate(
        val matchId: String,
        val game: TicTacGame,
        val type: GameUpdateType
    )

    enum class GameUpdateType {
        Created,
        Updated,
        Removed
    }
}