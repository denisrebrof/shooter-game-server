package com.denisrebrof.user.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.UserNotFoundDefaultResponse
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.repositories.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetUsernameRequestHandler @Autowired constructor(
    private val userRepository: IUserRepository
) : WSUserEmptyRequestHandler(WSCommand.GetUserName.id) {

    override fun handleMessage(userId: Long): ResponseState {
        val user = UserIdentity
            .fromUserId(userId)
            .let(userRepository::find)
            ?: return UserNotFoundDefaultResponse

        return ResponseState.CreatedResponse(user.username)
    }
}