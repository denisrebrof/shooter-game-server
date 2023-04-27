package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.fight.domain.model.FightGame
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UpdateGameUseCase @Autowired constructor(
    private val gamesRepository: FightGamesRepository
) {
    fun update(matchId: String, update: FightGame.() -> FightGame) {
        val game = gamesRepository.get(matchId) ?: return
        gamesRepository.set(matchId, update(game))
    }
}