package com.denisrebrof.user.gateways

import com.denisrebrof.commands.domain.model.ResponseErrorCodes
import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.gateways.WSSessionRequestHandler
import com.denisrebrof.simplestats.domain.ISimpleStatsReceiver
import com.denisrebrof.simplestats.domain.setPropertyString
import com.denisrebrof.user.domain.LogInUseCase
import com.denisrebrof.user.domain.model.AuthParams.*
import com.denisrebrof.user.domain.model.LoginResult
import com.denisrebrof.user.gateways.WSLogInRequestHandler.AuthParamsData
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WSLogInRequestHandler @Autowired constructor(
    private val logInUseCase: LogInUseCase,
    statsReceiver: ISimpleStatsReceiver
) : WSSessionRequestHandler<AuthParamsData>(WSCommand.LogIn.id) {

    private var successfulLoginsCount = 0L
    private var failedLoginsCount = 0L

    init {
        statsReceiver.setPropertyString("Successful logins") { successfulLoginsCount.toString() }
        statsReceiver.setPropertyString("Failed logins") { failedLoginsCount.toString() }
    }

    private val incorrectAuthParamsResponse = ResponseState.ErrorResponse(
        ResponseErrorCodes.Unauthorized.code,
        IllegalArgumentException("Invalid auth params format")
    )

    override fun parseData(data: String): AuthParamsData = Json.decodeFromString(data)

    override fun handleMessage(sessionId: String, data: AuthParamsData): ResponseState = with(data) {
        val params = when {
            localId.isNotBlank() -> Anonymous(localId)
            yandexId.isNotBlank() -> YandexId(yandexId)
            token.isNotBlank() -> Token(token)
            username.isNotBlank() && password.isNotBlank() -> UsernamePassword(username, password)
            else -> return@with incorrectAuthParamsResponse
        }
        return@with logInUseCase
            .login(params, sessionId)
            .also(::handleStatsCounter)
            .let(::createResponse)
            .let(Json::encodeToString)
            .let(ResponseState::CreatedResponse)
    }

    private fun createResponse(result: LoginResult) = when (result) {
        LoginResult.Failed -> LoginResponse(false)
        is LoginResult.Success -> LoginResponse(true, result.token, result.userId)
    }

    private fun handleStatsCounter(result: LoginResult) = when (result) {
        LoginResult.Failed -> failedLoginsCount += 1
        is LoginResult.Success -> successfulLoginsCount += 1
    }

    @Serializable
    data class LoginResponse(
        val authSuccessful: Boolean,
        val token: String = "",
        val userId: Long = -1L
    )

    @Serializable
    data class AuthParamsData(
        val yandexId: String = "",
        val localId: String = "",
        val username: String = "",
        val password: String = "",
        val token: String = "",
    )
}