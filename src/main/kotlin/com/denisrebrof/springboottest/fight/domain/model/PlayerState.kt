package com.denisrebrof.springboottest.fight.domain.model

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable


data class PlayerState(
    val intents: FighterIntent,
    private var internalState: FighterState
) {
    var state: FighterState
        get() = internalState
        set(value){
            val currentFightingState = internalState as? FighterState.Fighting
            if (currentFightingState != null && value !is FighterState.Fighting)
                currentFightingState.dispose()

            internalState = value
        }
}

data class FighterIntent(
    var attackDirection: AttackDirection,
    var movement: MovementDirection,
)

sealed class FighterState(open var position: Float) {
    data class Preparing(override var position: Float) : FighterState(position)

    data class Fighting(
        var movement: MovementDirection = MovementDirection.None,
        override var position: Float
    ) : FighterState(position) {
        private var internalAction: FightingAction = FightingAction.Idle

        private val attackingStateDisposable = CompositeDisposable()

        var action: FightingAction
            get() = internalAction
            set(value) {
                if (internalAction is FightingAction.Attacking && value !is FightingAction.Attacking)
                    attackingStateDisposable.clear()

                internalAction = value
            }

        fun doOnAttacking(disposable: Disposable) = attackingStateDisposable.add(disposable)

        fun dispose() {
            attackingStateDisposable.clear()
        }
    }

    data class Killed(
        var lastHitDirection: AttackDirection,
        override var position: Float
    ) : FighterState(position)
}

sealed class FightingAction {
    object Idle : FightingAction()
    data class GotHit(val direction: AttackDirection) : FightingAction()
    data class Attacking(
        val direction: AttackDirection,
        var step: AttackStep = AttackStep.Preparing,
    ) : FightingAction()
}

enum class AttackStep(val code: Long) {
    Preparing(0L),
    Punching(1L),
    Returning(2L)
}

enum class AttackDirection(val code: Long) {
    None(0L),
    UpperRight(1L),
    LowerRight(2L),
    LowerLeft(3L),
    UpperLeft(4L)
}

enum class MovementDirection(val code: Long) {
    None(0L),
    Forward(1L),
    Backward(2L)
}