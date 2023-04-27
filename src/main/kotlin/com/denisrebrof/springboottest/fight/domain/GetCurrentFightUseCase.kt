package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.fight.domain.model.FightGame
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetCurrentFightUseCase @Autowired constructor(
    private val gamesRepository: FightGamesRepository,
    private val matchRepository: IMatchRepository
) {
    fun get(userId: Long): FightGame? = matchRepository
        .getMatchIdByUserId(userId)
        ?.let(gamesRepository::get)
}