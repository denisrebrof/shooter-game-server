package com.denisrebrof.user.gateways

import com.denisrebrof.commands.domain.model.ResponseErrorCodes
import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.repositories.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetUsernameRequestHandler @Autowired constructor(
    private val userRepository: IUserRepository
) : WSUserEmptyRequestHandler(WSCommand.GetUserName.id) {

    private val userNotFoundResponse = ResponseState.ErrorResponse(
        ResponseErrorCodes.Internal.code,
        Exception("User not found!")
    )

    override fun handleMessage(userId: Long): ResponseState {
        val user = UserIdentity
            .fromUserId(userId)
            .let(userRepository::find)
            ?: return userNotFoundResponse

        return ResponseState.CreatedResponse(user.username)
    }
}