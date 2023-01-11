package com.denisrebrof.springboottest.commands.domain.model

enum class WSCommand(val id: Long) {
    LogIn(0L),
    GetUserData(1L),
    LobbyAction(2L),
    LobbyState(3L),
    GetMatch(4L),

    TicTacState(10L),
    TicTacCellUpdates(11L),
    TicTacTurnUpdates(12L),
    TicTacMakeTurn(13L),
    TicTacFinished(14L),

    BalanceState(15L),
}