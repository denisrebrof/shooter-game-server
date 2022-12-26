package com.denisrebrof.springboottest.commands.domain.model

enum class WSCommandId(val id: Long) {
    GetUserData(1L),
    LobbyAction(2L),
    LobbyState(3L),
    GetMatch(4L),

    TicTacState(10L),
    TicTacCellUpdates(11L),
    TicTacTurnUpdates(12L),
    TicTacMakeTurn(13L),
    TicTacFinished(14L),
}