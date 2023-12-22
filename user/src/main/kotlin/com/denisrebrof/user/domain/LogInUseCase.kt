package com.denisrebrof.user.domain

import com.denisrebrof.user.domain.model.AuthParams
import com.denisrebrof.user.domain.model.LoginResult
import com.denisrebrof.user.domain.model.UserIdentity
import com.denisrebrof.user.domain.model.UserIdentityType
import com.denisrebrof.user.domain.repositories.IUserRepository
import com.denisrebrof.user.domain.repositories.IWSUserSessionMappingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LogInUseCase @Autowired constructor(
    private val userRepository: IUserRepository,
    private val getOrCreateUserUseCase: GetOrCreateUserUseCase,
    private val sessionMappingRepository: IWSUserSessionMappingRepository,
    private val encoder: PasswordEncoder
) {

    fun login(
        authParams: AuthParams,
        sessionId: String
    ): LoginResult {
        val identity = when (authParams) {
            is AuthParams.Token -> UserIdentity(authParams.token, UserIdentityType.Token)
            is AuthParams.YandexId -> UserIdentity(authParams.id, UserIdentityType.YandexId)
            is AuthParams.Anonymous -> UserIdentity(authParams.id, UserIdentityType.LocalId)
            is AuthParams.UsernamePassword -> return basicLogin(authParams.username, authParams.password)
        }
        return identityLogin(identity, sessionId)
    }

    private fun identityLogin(identity: UserIdentity, sessionId: String): LoginResult {
        val user = getOrCreateUserUseCase
            .getOrCreate(identity)
            ?: return LoginResult.Failed

        userRepository.increaseLoginCount(identity)
        sessionMappingRepository.addMapping(user.id, sessionId)
        return LoginResult.Success("", user.id)
    }

    private fun basicLogin(username: String, password: String): LoginResult {
        val identity = UserIdentity(username, UserIdentityType.Username)
        val user = userRepository
            .find(identity)
            ?: return LoginResult.Failed

        val correctPassword = encoder
            .encode(password)
            .equals(user.password)

        if (!correctPassword)
            return LoginResult.Failed

        return LoginResult.Success("", user.id)
    }

    private fun tokenLogin(token: String): LoginResult = LoginResult.Failed
}