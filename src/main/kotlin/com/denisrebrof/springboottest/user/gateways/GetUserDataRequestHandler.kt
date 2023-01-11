package com.denisrebrof.springboottest.user.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseErrorCodes
import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.user.domain.model.UserIdentity
import com.denisrebrof.springboottest.user.domain.repositories.IUserRepository
import com.denisrebrof.springboottest.user.gateways.model.UserData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetUserDataRequestHandler @Autowired constructor(
    private val userRepository: IUserRepository
) : WSUserEmptyRequestHandler(WSCommand.GetUserData.id) {

    private val userNotFoundResponse = ResponseState.ErrorResponse(
        ResponseErrorCodes.Internal.code,
        Exception("User not found!")
    )

    override fun handleMessage(userId: Long): ResponseState {
        val user = UserIdentity
            .fromUserId(userId)
            .let(userRepository::find)
            ?: return userNotFoundResponse

        return UserData.fromUser(user)
            .let(Json::encodeToString)
            .let(ResponseState::CreatedResponse)
    }
}