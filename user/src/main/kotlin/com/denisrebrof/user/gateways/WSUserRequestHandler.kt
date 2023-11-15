package com.denisrebrof.user.gateways

import com.denisrebrof.commands.domain.model.ResponseErrorCodes
import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.user.domain.repositories.IWSUserSessionMappingRepository
import com.denisrebrof.commands.gateways.WSSessionRequestHandler
import org.springframework.beans.factory.annotation.Autowired

abstract class WSUserRequestHandler<MESSAGE_DATA : Any>(
    override val id: Long
) : WSSessionRequestHandler<MESSAGE_DATA>(id) {

    @Autowired
    lateinit var mappingRepository: IWSUserSessionMappingRepository

    private val unauthorizedResponse = ResponseState.ErrorResponse(
        ResponseErrorCodes.Unauthorized.code,
        Exception("Authorized user not found")
    )

    final override fun handleMessage(sessionId: String, data: MESSAGE_DATA): ResponseState {
        val userId = mappingRepository
            .getMapping(sessionId)
            ?: return unauthorizedResponse

        return handleMessage(userId, data)
    }

    abstract fun handleMessage(userId: Long, data: MESSAGE_DATA): ResponseState
}