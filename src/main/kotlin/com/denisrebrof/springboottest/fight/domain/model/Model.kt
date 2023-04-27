package com.denisrebrof.springboottest.fight.domain.model

data class FighterIntent(
    val attackDirection: AttackDirection,
    val movement: MovementDirection,
)

sealed class FighterState(open val position: Float) {
    data class Preparing(override val position: Float) : FighterState(position)

    data class Fighting(
        val movement: MovementDirection,
        val attack: AttackState,
        val gotHit: GotHitState,
        override val position: Float
    ) : FighterState(position)

    data class Killed(
        val lastHitDirection: AttackDirection,
        override val position: Float
    ) : FighterState(position)
}

sealed class GotHitState {
    object Idle : GotHitState()
    data class GotHit(val direction: AttackDirection) : GotHitState()
}

sealed class AttackState {
    object Idle : AttackState()
    data class Attacking(val step: AttackStep) : AttackState()
}

enum class AttackStep(val code: Long) {
    Preparing(0L),
    Punching(1L),
    Returning(2L)
}

enum class AttackDirection(val code: Long) {
    UpperRight(0L),
    LowerRight(1L),
    LowerLeft(2L),
    UpperLeft(3L)
}

enum class MovementDirection(val code: Long) {
    None(0L),
    Forward(1L),
    Backward(2L)
}