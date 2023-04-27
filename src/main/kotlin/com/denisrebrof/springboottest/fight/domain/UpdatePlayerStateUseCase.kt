package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.fight.domain.model.PlayerState
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UpdatePlayerStateUseCase @Autowired constructor(
    private val updateGameUseCase: UpdateGameUseCase,
    private val matchRepository: IMatchRepository,
) {
    fun update(
        userId: Long,
        action: (PlayerState) -> PlayerState
    ) {
        val matchId = matchRepository.getMatchIdByUserId(userId) ?: return
        updateGameUseCase.update(matchId) {
            val states = playerStates.toMutableMap()
            val newState = states[userId] ?: return@update this
            states[userId] = action(newState)
            copy(playerStates = states)
        }
    }
}