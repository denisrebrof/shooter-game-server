package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.fight.domain.model.FighterIntent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UpdateFighterIntentUseCase @Autowired constructor(
    private val updatePlayerStateUseCase: UpdatePlayerStateUseCase
) {
    fun update(
        userId: Long,
        update: (FighterIntent) -> FighterIntent
    ) = updatePlayerStateUseCase.update(userId) { state ->
        state.copy(intents = update(state.intents))
    }
}