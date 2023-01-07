package com.denisrebrof.springboottest.user.domain

import com.denisrebrof.springboottest.user.domain.model.AuthParams
import com.denisrebrof.springboottest.user.domain.model.LoginResult
import com.denisrebrof.springboottest.user.domain.model.User
import com.denisrebrof.springboottest.user.domain.repositories.IUserRepository
import com.denisrebrof.springboottest.user.domain.repositories.IWSUserSessionMappingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LogInUseCase @Autowired constructor(
    private val userRepository: IUserRepository,
    private val sessionMappingRepository: IWSUserSessionMappingRepository,
    private val encoder: PasswordEncoder
) {

    fun login(
        authParams: AuthParams,
        sessionId: String
    ): LoginResult = when (authParams) {
        is AuthParams.Token -> tokenLogin(authParams.token)
        is AuthParams.YandexId -> yandexLogin(authParams.id, sessionId)
        is AuthParams.Anonymous -> anonymousLogin(authParams.id, sessionId)
        is AuthParams.UsernamePassword -> defaultLogin(authParams.username, authParams.password)
    }

    private fun anonymousLogin(localId: String, sessionId: String): LoginResult {
        if (localId.isBlank())
            return LoginResult.Failed

        val user = userRepository
            .findUserByLocalId(localId)
            ?: createLocalUser(localId)
        val userId = user.id ?: return LoginResult.Failed
        sessionMappingRepository.addMapping(userId, sessionId)
        return LoginResult.Success("")
    }

    private fun yandexLogin(yandexId: String, sessionId: String): LoginResult {
        val user = userRepository
            .findUserByYandexId(yandexId)
            ?: createYandexUser(yandexId)
        val userId = user.id ?: return LoginResult.Failed
        sessionMappingRepository.addMapping(userId, sessionId)
        return LoginResult.Success("")
    }

    private fun createLocalUser(localId: String) = User(
        username = "User_$localId",
        localId = localId
    ).let(userRepository::save)

    private fun createYandexUser(yandexId: String) = User(
        username = "User_$yandexId",
        yandexId = yandexId
    ).let(userRepository::save)

    private fun defaultLogin(username: String, password: String): LoginResult {
        val user = userRepository
            .findUserByUsername(username)
            .firstOrNull()
            ?: return LoginResult.Failed

        val correctPassword = encoder
            .encode(password)
            .equals(user.password)

        if (!correctPassword)
            return LoginResult.Failed

        return LoginResult.Success("")
    }

    private fun tokenLogin(token: String): LoginResult {
        return LoginResult.Failed
    }
}