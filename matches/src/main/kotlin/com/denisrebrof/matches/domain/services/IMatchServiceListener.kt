package com.denisrebrof.matches.domain.services

import com.denisrebrof.matches.domain.model.Match

interface IMatchServiceListener {
    fun onMatchStarted(match: Match)
    fun onMatchFinished(match: Match)
    fun onJoined(match: Match, vararg participantIds: Long)
    fun onLeft(match: Match, vararg participantIds: Long)
}

object EmptyMatchServiceListener : IMatchServiceListener {
    override fun onMatchStarted(match: Match) = Unit
    override fun onMatchFinished(match: Match) = Unit
    override fun onJoined(match: Match, vararg participantIds: Long) = Unit
    override fun onLeft(match: Match, vararg participantIds: Long) = Unit
}