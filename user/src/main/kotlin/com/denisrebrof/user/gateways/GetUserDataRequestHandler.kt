package com.denisrebrof.user.gateways

import com.denisrebrof.commands.domain.model.ResponseErrorCodes
import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.user.domain.repositories.IUserRepository
import com.denisrebrof.user.gateways.model.UserDataResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetUserDataRequestHandler @Autowired constructor(
    private val userRepository: IUserRepository
) : WSUserRequestHandler<Long>(WSCommand.GetUserData.id) {

    override fun parseData(data: String): Long = data.toLongOrNull() ?: -1L

    private val wrongDataResponse = ResponseState.ErrorResponse(
        ResponseErrorCodes.Internal.code,
        Exception("User not found!")
    )

    private val userNotFoundResponse = ResponseState.ErrorResponse(
        ResponseErrorCodes.Internal.code,
        Exception("User not found!")
    )

    override fun handleMessage(userId: Long, data: Long): ResponseState {
        if (data < 0L)
            return wrongDataResponse

        val user = UserIdentity
            .fromUserId(data)
            .let(userRepository::find)
            ?: return userNotFoundResponse

        return UserDataResponse.fromUser(user)
            .let(Json::encodeToString)
            .let(ResponseState::CreatedResponse)
    }
}