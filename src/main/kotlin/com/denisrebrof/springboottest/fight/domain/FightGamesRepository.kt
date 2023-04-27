package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.fight.domain.model.FightGame
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.stereotype.Service
import java.util.*

@Service
class FightGamesRepository {
    private val matchIdToGame = Collections.synchronizedMap(mutableMapOf<String, FightGame>())

    private val updates = PublishProcessor.create<GameUpdate>()

    fun set(matchId: String, game: FightGame) {
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

    fun get(matchId: String): FightGame? = matchIdToGame[matchId]

    fun getUpdates(): Flowable<GameUpdate> = updates

    data class GameUpdate(
        val matchId: String,
        val game: FightGame,
        val type: GameUpdateType
    )

    enum class GameUpdateType {
        Created,
        Updated,
        Removed
    }
}