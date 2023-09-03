package com.denisrebrof.springboottest.shooter

import DisposableService
import ShooterGame
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate.UpdateType
import io.reactivex.rxjava3.disposables.CompositeDisposable
import model.ShooterGameActions
import model.ShooterGameIntents
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import subscribeDefault
import java.util.concurrent.TimeUnit

@Service
class ShooterGameService @Autowired constructor(
    private val matchRepository: IMatchRepository,
    private val createGameUseCase: CreateShooterGameUseCase
) : DisposableService() {

    private val matchIdToGameMap = mutableMapOf<String, ShooterGame>()

    private val clearFinishedGameDelayMs: Long = 15000L

    final override val handler = CompositeDisposable()

    init {
        matchRepository
            .getMatchUpdates()
            .subscribeDefault(::onMatchUpdate)
            .let(handler::add)
    }

    private fun onMatchUpdate(update: MatchUpdate) = when (update.type) {
        UpdateType.Created -> addGame(update.match)
        UpdateType.Removed -> clearGame(update.match.id)
    }

    private fun clearGame(matchId: String) = matchIdToGameMap
        .remove(matchId)
        ?.dispose()
        ?: Unit

    private fun addGame(match: Match) = createGameUseCase
        .create(match.participantIds)
        .let { matchIdToGameMap[match.id] = it }
        .let { createClearFinishedMatchHandler(match.id) }

    private fun createClearFinishedMatchHandler(matchId: String) {
        val game = matchIdToGameMap[matchId] ?: return
        game
            .actions
            .filter(ShooterGameActions.LifecycleCompleted::equals)
            .delay(clearFinishedGameDelayMs, TimeUnit.MILLISECONDS)
            .subscribeDefault { matchRepository.remove(matchId) }
            .let(game::add)
    }

    fun submitIntent(matchId: String, intent: ShooterGameIntents) = matchIdToGameMap[matchId]
        ?.submit(intent)
        ?: Unit

//    fun removePlayer(matchId: String, playerId: Long) = matchIdToGameMap[matchId]?.removePlayer(playerId) ?: Unit
}