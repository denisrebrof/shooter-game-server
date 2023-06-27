package com.denisrebrof.springboottest.hideandseekgame.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.hideandseekgame.domain.core.PlayerInput
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.user.gateways.WSUserRequestHandler

abstract class PlayerInputRequestHandler<INPUT : Any>(
    commandId: Long,
    private val matchRepository: IMatchRepository,
    private val manager: GameManager
) : WSUserRequestHandler<INPUT>(commandId) {

    abstract fun createInput(userId: Long, data: INPUT): PlayerInput

    override fun handleMessage(userId: Long, data: INPUT): ResponseState {
        val matchId = matchRepository
            .getMatchIdByUserId(userId)
            ?: return ResponseState.NoResponse

        val input = createInput(userId, data)
        manager.submitInput(matchId, input)
        return ResponseState.NoResponse
    }
}