package com.denisrebrof.springboottest.tictac.gateways

import com.denisrebrof.springboottest.matches.domain.MatchRepository
import com.denisrebrof.springboottest.matches.domain.MatchRepository.MatchUpdate.UpdateType
import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.tictac.domain.TicTacGameRepository
import com.denisrebrof.springboottest.tictac.domain.model.TicTacGame
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TicTacGameToMatchHandler @Autowired constructor(
    matchRepository: MatchRepository,
    private val gamesRepository: TicTacGameRepository
) : DisposableService() {

    override val handler: Disposable = matchRepository
        .getMatchUpdates()
        .onBackpressureBuffer()
        .subscribeDefault(::handleMatchUpdate)

    private fun handleMatchUpdate(update: MatchRepository.MatchUpdate) = when (update.type) {
        UpdateType.Created -> createGame(update.match)
        UpdateType.Removed -> gamesRepository.remove(update.match.id)
    }

    private fun createGame(match: Match) {
        val newGame = TicTacGame(match.participantIds)
        gamesRepository.set(match.id, newGame)
    }
}