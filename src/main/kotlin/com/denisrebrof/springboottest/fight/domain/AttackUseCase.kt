package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.fight.domain.AttackUseCase.AddAttackResult.*
import com.denisrebrof.springboottest.fight.domain.model.*
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.core.Completable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.reflect.safeCast

@Service
class AttackUseCase @Autowired constructor(
    private val getCurrentFightUseCase: GetCurrentFightUseCase,
    private val punchUseCase: PunchUseCase
) {

    companion object {
        private const val PREPARE_DURATION_MS = 500L
        private const val PUNCHING_DURATION_MS = 200L
        private const val RETURN_DURATION_MS = 300L
    }

    fun add(userId: Long, direction: AttackDirection): AddAttackResult {
        val game = getCurrentFightUseCase.get(userId) ?: return FightNotFound
        val playerState = game.playerStates[userId] ?: return UserNotFound
        if (game.state != GameState.Playing)
            return InvalidGameState

        val fightingState = playerState
            .state
            .let(FighterState.Fighting::class::safeCast)
            ?: return InvalidPlayerState

        if (fightingState.action !is FightingAction.Idle)
            return InvalidPlayerState

        val attackAction = FightingAction
            .Attacking(direction)
            .also(fightingState::action::set)

        Completable
            .timer(PREPARE_DURATION_MS, TimeUnit.MILLISECONDS)
            .doOnComplete { attackAction.step = AttackStep.Punching }
            .delay(PUNCHING_DURATION_MS, TimeUnit.MILLISECONDS)
            .doOnComplete {
                punchUseCase.addDamage(game, userId)
                attackAction.step = AttackStep.Returning
            }
            .delay(RETURN_DURATION_MS, TimeUnit.MILLISECONDS)
            .doOnComplete { fightingState.action = FightingAction.Idle }
            .subscribeDefault()
            .let(fightingState::doOnAttacking)

        return Executed
    }

    enum class AddAttackResult {
        Executed,
        InvalidPlayerState,
        InvalidGameState,
        UserNotFound,
        FightNotFound
    }
}