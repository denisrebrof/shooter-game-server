package com.denisrebrof.springboottest.tictac.domain

import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.tictac.domain.model.TicTacGame
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TicTacUserGameUseCase @Autowired constructor(
    private val gameRepository: TicTacGameRepository,
    private val matchRepository: IMatchRepository
) {
    fun get(userId: Long): TicTacGame? = matchRepository
        .getMatchIdByUserId(userId)
        ?.let(gameRepository::get)

    fun set(userId: Long, game: TicTacGame) {
        val matchId = matchRepository.getMatchIdByUserId(userId) ?: return
        return gameRepository.set(matchId, game)
    }
}