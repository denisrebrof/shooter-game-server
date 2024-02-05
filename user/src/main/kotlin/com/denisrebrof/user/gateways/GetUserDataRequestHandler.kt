package com.denisrebrof.user.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.UserNotFoundDefaultResponse
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.toResponse
import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.repositories.IUserRepository
import com.denisrebrof.user.gateways.model.UserDataResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetUserDataRequestHandler @Autowired constructor(
    private val userRepository: IUserRepository
) : WSUserRequestHandler<Long>(WSCommand.GetUserData.id) {

    override fun parseData(data: String): Long = data.toLongOrNull() ?: -1L

    override fun handleMessage(userId: Long, data: Long): ResponseState {
        if (data < 0L)
            return "Bot_"
                .plus(-data)
                .let(::UserDataResponse)
                .toResponse()

        val user = UserIdentity
            .fromUserId(data)
            .let(userRepository::find)
            ?: return UserNotFoundDefaultResponse

        return UserDataResponse
            .fromUser(user)
            .toResponse()
    }
}