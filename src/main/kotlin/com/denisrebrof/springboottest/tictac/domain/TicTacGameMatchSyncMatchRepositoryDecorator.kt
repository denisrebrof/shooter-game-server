package com.denisrebrof.springboottest.tictac.domain

import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.tictac.domain.model.TicTacGame
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class TicTacGameMatchSyncMatchRepositoryDecorator @Autowired constructor(
    @Qualifier("Base")
    private val target: IMatchRepository,
    private val gameRepository: TicTacGameRepository
) : IMatchRepository by target {
    override fun add(match: Match) {
        createGame(match)
        target.add(match)
    }

    override fun remove(matchId: String) {
        gameRepository.remove(matchId)
        target.remove(matchId)
    }

    private fun createGame(match: Match) {
        val newGame = TicTacGame(match.participantIds)
        gameRepository.set(match.id, newGame)
    }
}