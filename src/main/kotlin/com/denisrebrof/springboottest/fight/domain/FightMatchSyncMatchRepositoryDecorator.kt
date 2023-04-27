package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.matches.domain.model.Match
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class FightMatchSyncMatchRepositoryDecorator @Autowired constructor(
    @Qualifier("Base")
    private val target: IMatchRepository,
    private val gameRepository: FightGamesRepository,
    private val buildGameUseCase: BuildFightGameUseCase,
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
        val newGame = buildGameUseCase.createGame(match.participantIds)
        gameRepository.set(match.id, newGame)
    }
}