package com.denisrebrof.springboottest.fight.gateways.model

data class FightFinishedResponse(
    private val finished: Boolean,
    private val isWinner: Boolean,
    private val isDraw: Boolean,
    private val reward: Long,
)
