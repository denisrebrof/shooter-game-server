package com.denisrebrof.springboottest.mvigame

import DisposableService
import MVIGameHandler
import com.denisrebrof.springboottest.game.domain.GameBase
import com.denisrebrof.springboottest.hideandseekgame.domain.CreateGameUseCase
import com.denisrebrof.springboottest.hideandseekgame.domain.NotifyGameEventUseCase
import com.denisrebrof.springboottest.hideandseekgame.domain.core.GameState
import com.denisrebrof.springboottest.hideandseekgame.domain.core.PlayerInput
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.RoundEvent
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import subscribeDefault
import java.util.concurrent.TimeUnit

@Service
class MVIGameService @Autowired constructor(
    private val matchRepository: IMatchRepository,
    private val createGameUseCase: CreateGameUseCase,
    private val notifyGameEventUseCase: NotifyGameEventUseCase
) : DisposableService() {

//    private val matchIdToGameMap = mutableMapOf<String, MVIGameHandler<>>()

    private val clearFinishedGameDelayMs: Long = 15000L

    final override val handler = CompositeDisposable()

    init {
//        matchRepository
//            .getMatchUpdates()
//            .subscribeDefault(::onMatchUpdate)
//            .let(handler::add)
    }

//    private fun onMatchUpdate(update: MatchUpdate) = when (update.type) {
//        MatchUpdate.UpdateType.Created -> addGame(update.match)
//        MatchUpdate.UpdateType.Removed -> clearGame(update.match.id)
//    }

    private fun clearGame(matchId: String) {
//        matchIdToGameMap.remove(matchId)?.stop()
    }

    private fun GameBase<GameState, PlayerInput, RoundEvent>.handleEvents(matchId: String): Boolean {
        val finishHandler = getEvents()
            .ofType(RoundEvent.Finished::class.java)
            .firstElement()
            .ignoreElement()
            .delay(clearFinishedGameDelayMs, TimeUnit.MILLISECONDS)

        val roundEventsHandler = getEvents()
            .doOnNext { roundEvent -> notifyGameEventUseCase.notify(roundEvent, matchId) }
            .ignoreElements()

        val stateEventsHandler = stateFlow
            .doOnNext { state -> notifyGameEventUseCase.notify(state, matchId) }
            .ignoreElements()

        return Completable.ambArray(
            roundEventsHandler,
            stateEventsHandler,
            finishHandler,
        )
            .doOnComplete { matchRepository.remove(matchId) }
            .subscribeDefault()
            .let(handler::add)
    }

    private fun GameBase<GameState, PlayerInput, RoundEvent>.setupEventsHandler(
        matchId: String
    ): GameBase<GameState, PlayerInput, RoundEvent> {
        handleEvents(matchId)
        return this
    }

//    private fun addGame(match: Match) = createGameUseCase
//        .create(match.participantIds)
//        .setupEventsHandler(match.id)
//        .also(GameBase<GameState, PlayerInput, RoundEvent>::start)
//        .let { matchIdToGameMap[match.id] = it }

//    fun submitInput(matchId: String, input: PlayerInput) = matchIdToGameMap[matchId]?.submitInput(input) ?: Unit

//    fun removePlayer(matchId: String, playerId: Long) = matchIdToGameMap[matchId]?.removePlayer(playerId) ?: Unit
}