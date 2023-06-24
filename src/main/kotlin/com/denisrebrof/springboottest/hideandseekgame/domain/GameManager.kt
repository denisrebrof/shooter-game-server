package com.denisrebrof.springboottest.hideandseekgame.domain

import com.denisrebrof.springboottest.hideandseekgame.domain.core.HNSGame
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.RoundEvent
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate.UpdateType
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class GameManager @Autowired constructor(
    private val matchRepository: IMatchRepository,
    private val createGameUseCase: CreateGameUseCase,
    private val notifyGameEventUseCase: NotifyGameEventUseCase
) : DisposableService() {

    private val matchIdToGameMap = mutableMapOf<String, HNSGame>()

    private val clearFinishedGameDelayMs: Long = 1000L

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

    private fun clearGame(matchId: String) {
        matchIdToGameMap.remove(matchId)?.stop()
    }

    private fun HNSGame.handleEvents(matchId: String): Boolean {
        val finishHandler = getRoundEvents()
            .ofType(RoundEvent.Finished::class.java)
            .firstElement()
            .ignoreElement()

        val roundEventsHandler = getRoundEvents()
            .doOnNext { roundEvent -> notifyGameEventUseCase.notify(roundEvent, matchId) }
            .ignoreElements()

        val stateEventsHandler = stateFlow
            .doOnNext { state -> notifyGameEventUseCase.notify(state, matchId) }
            .ignoreElements()

        return Completable.ambArray(
            finishHandler,
            roundEventsHandler,
            stateEventsHandler
        )
            .delay(clearFinishedGameDelayMs, TimeUnit.MILLISECONDS)
            .doOnComplete { clearGame(matchId) }
            .subscribeDefault()
            .let(handler::add)
    }

    private fun HNSGame.setupEventsHandler(matchId: String): HNSGame {
        handleEvents(matchId)
        return this
    }

    private fun addGame(match: Match) = createGameUseCase
        .create(match.participantIds)
        .setupEventsHandler(match.id)
        .let { matchIdToGameMap[match.id] = it }
}