package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.fight.domain.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BuildFightGameUseCase @Autowired constructor(

) {
    companion object {
        private val defaultDirection = AttackDirection.UpperRight
        private const val defaultOffset: Float = 1.5f
    }

    fun createGame(participantIds: List<Long>): FightGame {
        val playerStates = participantIds.associateWith { playerId ->
            val intents = FighterIntent(defaultDirection, MovementDirection.None)
            val state = FighterState.Preparing(defaultOffset)
            PlayerState(intents, state)
        }
        return FightGame(playerStates)
    }

}