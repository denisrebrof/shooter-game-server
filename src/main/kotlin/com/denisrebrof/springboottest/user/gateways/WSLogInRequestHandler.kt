package com.denisrebrof.springboottest.user.gateways

import com.denisrebrof.springboottest.commands.domain.model.ResponseErrorCodes
import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.commands.gateways.WSSessionRequestHandler
import com.denisrebrof.springboottest.user.domain.LogInUseCase
import com.denisrebrof.springboottest.user.domain.model.AuthParams.*
import com.denisrebrof.springboottest.user.domain.model.LoginResult
import com.denisrebrof.springboottest.user.gateways.WSLogInRequestHandler.AuthParamsData
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WSLogInRequestHandler @Autowired constructor(
    private val logInUseCase: LogInUseCase
) : WSSessionRequestHandler<AuthParamsData>(WSCommand.LogIn.id) {

    private val incorrectAuthParamsResponse = ResponseState.ErrorResponse(
        ResponseErrorCodes.Unauthorized.code,
        IllegalArgumentException("Invalid auth params format")
    )

    override fun parseData(data: String): AuthParamsData = Json.decodeFromString(data)

    override fun handleMessage(sessionId: String, data: AuthParamsData): ResponseState = with(data) {
        val params = when {
            yandexId.isNotBlank() -> YandexId(yandexId)
            token.isNotBlank() -> Token(token)
            username.isNotBlank() && password.isNotBlank() -> UsernamePassword(username, password)
            else -> return@with incorrectAuthParamsResponse
        }
        return@with logInUseCase
            .login(params, sessionId)
            .let(::createResponse)
            .let(Json::encodeToString)
            .let(ResponseState::CreatedResponse)
    }

    private fun createResponse(result: LoginResult) = when (result) {
        LoginResult.Failed -> LoginResponse(false)
        is LoginResult.Success -> LoginResponse(true, result.token)
    }

    @Serializable
    data class LoginResponse(
        val authSuccessful: Boolean,
        val token: String = ""
    )

    @Serializable
    data class AuthParamsData(
        val yandexId: String = "",
        val username: String = "",
        val password: String = "",
        val token: String = "",
    )
}