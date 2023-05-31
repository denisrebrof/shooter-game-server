package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.fight.domain.PunchUseCase.PunchResult.*
import com.denisrebrof.springboottest.fight.domain.model.AttackDirection
import com.denisrebrof.springboottest.fight.domain.model.FightGame
import com.denisrebrof.springboottest.fight.domain.model.FighterState
import com.denisrebrof.springboottest.fight.domain.model.FightingAction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.reflect.safeCast

@Service
class PunchUseCase @Autowired constructor(
    private val finishFightUseCase: FinishFightUseCase
) {

    companion object {
        private const val damageDistance = 10f
    }

    fun punch(game: FightGame, sourceUserId: Long) {
        val result = doPunch(game, sourceUserId)
        when(result) {
            InvalidState -> return
            Missed -> TODO()
            Blocked -> TODO()
            Damaged -> TODO()
            Killed -> TODO()
        }
    }

    private fun doPunch(game: FightGame, sourceUserId: Long): PunchResult = game.run {
        val sourceState = playerStates[sourceUserId] ?: return@run InvalidState
        val opponentState = playerStates.minus(sourceUserId).values.firstOrNull() ?: return@run InvalidState

        val sourceFightingState = sourceState
            .state
            .let(FighterState.Fighting::class::safeCast)
            ?: return@run InvalidState

        val opponentFightingState = opponentState
            .state
            .let(FighterState.Fighting::class::safeCast)
            ?: return@run InvalidState

        val sourceFightingAction = sourceFightingState
            .action
            .let(FightingAction.Attacking::class::safeCast)
            ?: return@run InvalidState

        val distance = Math.abs(sourceFightingState.position - opponentFightingState.position)
        if(distance > damageDistance)
            return@run Missed

        val blockDirection = when (opponentFightingState.action) {
            FightingAction.Idle -> opponentState.intents.attackDirection
            else -> AttackDirection.None
        }

        if (blockDirection == sourceFightingAction.direction)
            return@run Blocked

        //TODO: add hp system
        return@run Killed
    }

    private enum class PunchResult {
        InvalidState,
        Missed,
        Blocked,
        Damaged,
        Killed
    }
}