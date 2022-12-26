package com.denisrebrof.springboottest.userdata.gateways

import com.denisrebrof.springboottest.commands.domain.model.WSCommandId
import com.denisrebrof.springboottest.commands.gateways.WSEmptyRequestHandler
import com.denisrebrof.springboottest.user.IUserRepository
import com.denisrebrof.springboottest.userdata.domain.model.UserData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetUserDataRequestHandler @Autowired constructor(
    private val userRepository: IUserRepository
) : WSEmptyRequestHandler(WSCommandId.GetUserData.id) {

    override fun handleMessage(userId: Long): ResponseState = userRepository
        .findUserById(userId)
        .let(UserData.Companion::fromUser)
        .let(Json::encodeToString)
        .let(ResponseState::CreatedResponse)
}