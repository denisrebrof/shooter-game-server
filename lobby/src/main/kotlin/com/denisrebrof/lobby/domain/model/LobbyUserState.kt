package com.denisrebrof.lobby.domain.model

enum class LobbyUserState(val code: Long) {
    Joined(1L),
    NotIn(2L)
}