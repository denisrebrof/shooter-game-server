package com.denisrebrof.commands.domain.model

enum class WSCommand(val id: Long) {
    LogIn(0L),
    GetUserData(1L),
    LobbyAction(2L),
    LobbyState(3L),
    GetMatch(4L),
    BalanceState(5L),

    Ping(7L),

    GameState(6L),
    ActionShoot(9L),
    ActionHit(10L),

    IntentSubmitPosition(11L),
    IntentSelectWeapon(12L),
    IntentShoot(13L),
    IntentHit(14L),

    LeaveMatch(15L),
    JoinMatch(16L),
    TimeLeft(17L),
    GetGames(18L),

    ActionJoinedStateChange(19L),

    GetUserName(20L),
    SetUserName(21L),
}