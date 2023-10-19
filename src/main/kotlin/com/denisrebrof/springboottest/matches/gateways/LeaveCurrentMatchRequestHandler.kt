package com.denisrebrof.springboottest.matches.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.user.gateways.WSUserEmptyRequestHandler
import org.springframework.beans.factory.annotation.Autowired

class LeaveCurrentMatchRequestHandler @Autowired constructor(
    private val matchRepository: IMatchRepository,
) : WSUserEmptyRequestHandler(WSCommand.LeaveMatch.id) {
    override fun handleMessage(userId: Long): ResponseState {
        matchRepository.
        return ResponseState.CreatedResponse("0")
    }
}