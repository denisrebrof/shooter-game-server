package com.denisrebrof.springboottest.tictac.gateways

import com.denisrebrof.springboottest.matches.domain.MatchRepository
import com.denisrebrof.springboottest.tictac.domain.TicTacGameRepository
import com.denisrebrof.springboottest.tictac.domain.TicTacGameRepository.GameUpdateType
import com.denisrebrof.springboottest.tictac.domain.model.GameState
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.filterNot
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class TicTacRemoveFinishedGameHandler @Autowired constructor(
    gameRepository: TicTacGameRepository,
    private val matchRepository: MatchRepository
) : DisposableService() {

    private val disposedMatchIds = mutableSetOf<String>()

    override val handler: Disposable = gameRepository
        .getUpdates()
        .filter { it.type == GameUpdateType.Updated }
        .filter { it.game.state is GameState.Finished }
        .map(TicTacGameRepository.GameUpdate::matchId)
        .filterNot(disposedMatchIds::contains)
        .flatMapMaybe(::startMatchDisposeTimer)
        .onBackpressureBuffer()
        .subscribeDefault(::disposeMatch)

    private fun startMatchDisposeTimer(matchId: String): Maybe<String> = Maybe
        .timer(matchDisposeDelay, TimeUnit.MILLISECONDS)
        .map { matchId }

    private fun disposeMatch(matchId: String) {
        matchRepository.remove(matchId)
        disposedMatchIds.remove(matchId)
    }

    companion object {
        private const val matchDisposeDelay = 10 * 1000L
    }
}