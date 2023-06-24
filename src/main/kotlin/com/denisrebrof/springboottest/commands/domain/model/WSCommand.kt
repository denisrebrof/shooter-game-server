package com.denisrebrof.springboottest.commands.domain.model

enum class WSCommand(val id: Long) {
    LogIn(0L),
    GetUserData(1L),
    LobbyAction(2L),
    LobbyState(3L),
    GetMatch(4L),
    BalanceState(5L),

    GameState(6L),
    RoundUpdate(7L),
    Movement(8L),
    Catch(9L),
    LayDown(10L),
}