package com.denisrebrof.springboottest.hideandseekgame.gateways

import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.hideandseekgame.domain.core.PlayerInput.Lay
import com.denisrebrof.springboottest.hideandseekgame.gateways.PlayerLayInputRequestHandler.LayInputRequestData
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlayerLayInputRequestHandler @Autowired constructor(
    matchRepository: IMatchRepository,
    manager: GameManager,
) : PlayerInputRequestHandler<LayInputRequestData>(WSCommand.LayDown.id, matchRepository, manager) {
    override fun parseData(data: String): LayInputRequestData = Json.decodeFromString(data)

    override fun createInput(userId: Long, data: LayInputRequestData) = Lay(userId, data.targetId, data.placeId)

    data class LayInputRequestData(
        val targetId: Long,
        val placeId: Long
    )
}