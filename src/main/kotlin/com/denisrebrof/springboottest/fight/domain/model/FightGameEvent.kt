package com.denisrebrof.springboottest.fight.domain.model

sealed class FightGameEvent {
    data class PlayerEvent(
        val userId: Long,
        val eventType: PlayerFightEventType
    )

    enum class PlayerFightEventType {
        Missed,
        Blocked,
        GotHit
    }
}
