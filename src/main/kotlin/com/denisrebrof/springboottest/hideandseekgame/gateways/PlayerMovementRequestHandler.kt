package com.denisrebrof.springboottest.hideandseekgame.gateways

import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.hideandseekgame.domain.core.PlayerInput
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlayerMovementRequestHandler @Autowired constructor(
    matchRepository: IMatchRepository,
    manager: GameManager,
) : PlayerInputRequestHandler<Transform>(WSCommand.Movement.id, matchRepository, manager) {
    override fun parseData(data: String): Transform = Json.decodeFromString(data)

    override fun createInput(userId: Long, data: Transform) = PlayerInput.Movement(userId, data)
}