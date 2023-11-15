package com.denisrebrof.matches.domain.model

interface IParticipantsHandler {
    fun addPlayers(vararg players: Long)
    fun removePlayers(vararg players: Long)
}