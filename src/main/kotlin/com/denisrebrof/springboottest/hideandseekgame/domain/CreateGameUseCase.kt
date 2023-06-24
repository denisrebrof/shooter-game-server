package com.denisrebrof.springboottest.hideandseekgame.domain

import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.hideandseekgame.domain.core.GameSettings
import com.denisrebrof.springboottest.hideandseekgame.domain.core.HNSGame
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.Character
import com.denisrebrof.springboottest.hideandseekgame.domain.core.model.Role
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
        sleepPlaces = mapOf()
    )

    fun create(participantIds: List<Long>): HNSGame {
        val playerIds = participantIds.toSet()
        return HNSGame(playerIds, defaultSettings)
    }
}