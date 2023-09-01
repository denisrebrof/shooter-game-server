package com.denisrebrof.springboottest.hideandseekgame.domain

import Transform
import com.denisrebrof.springboottest.game.domain.GameBase
import com.denisrebrof.springboottest.hideandseekgame.domain.core.GameSettings
import com.denisrebrof.springboottest.hideandseekgame.domain.core.GameState
import com.denisrebrof.springboottest.hideandseekgame.domain.core.HNSGame
import com.denisrebrof.springboottest.hideandseekgame.domain.core.PlayerInput
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.Character
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.Role
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.RoundEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CreateGameUseCase @Autowired constructor() {

    private val defaultTransform = Transform(0f, 0f, 0f, 0f)
    private val defaultSettings = GameSettings(
        roles = listOf(
            Role(Character.Hider1, defaultTransform, false),
            Role(Character.Seeker1, defaultTransform.copy(x = 0.5f), true)
        ),
        sleepPlaces = mapOf(),
        searchingDurationMs = 1000 * 60 * 15
    )

    fun create(participantIds: List<Long>): GameBase<GameState, PlayerInput, RoundEvent> {
        val playerIds = participantIds.toSet()
        return HNSGame(playerIds, defaultSettings)
    }
}