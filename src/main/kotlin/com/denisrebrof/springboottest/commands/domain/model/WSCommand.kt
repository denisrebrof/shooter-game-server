package com.denisrebrof.springboottest.commands.domain.model

enum class WSCommand(val id: Long) {
    LogIn(0L),
    GetUserData(1L),
    LobbyAction(2L),
    LobbyState(3L),
    GetMatch(4L),
    BalanceState(5L),

    FightFinished(6L),

    SetMovementData(7L),
    SetAttackDirection(8L),
    AddAttack(13L),

    GetFightState(9L),
    FightGameStateUpdate(10L),
    OpponentStateUpdate(11L),
    PlayerStateUpdate(12L),
    FightGameEvent(13L),
}