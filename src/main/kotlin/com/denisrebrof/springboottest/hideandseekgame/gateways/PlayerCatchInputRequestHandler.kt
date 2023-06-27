package com.denisrebrof.springboottest.hideandseekgame.gateways

import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.hideandseekgame.domain.core.PlayerInput
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlayerCatchInputRequestHandler @Autowired constructor(
    matchRepository: IMatchRepository,
    manager: GameManager,
) : PlayerInputRequestHandler<Long>(WSCommand.Catch.id, matchRepository, manager) {
    override fun parseData(data: String): Long = data.toLong()

    override fun createInput(userId: Long, data: Long) = PlayerInput.Catch(userId, data)
}