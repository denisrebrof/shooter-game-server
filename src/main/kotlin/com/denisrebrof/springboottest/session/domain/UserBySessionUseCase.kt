package com.denisrebrof.springboottest.session.domain

import com.denisrebrof.springboottest.user.IUserRepository
import com.denisrebrof.springboottest.user.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import kotlin.reflect.safeCast

@Service
class UserBySessionUseCase @Autowired constructor(
    private val userRepository: IUserRepository,
) {
    fun getUser(session: WebSocketSession): GetUserResult {
        val principal = session.principal ?: return GetUserResult.EmptyPrincipal

        val user = userRepository
            .findUserByUsername(principal.name)
            .firstOrNull()
            ?: return GetUserResult.UserNotFound

        return GetUserResult.Success(user)
    }

    fun getUserNullable(session: WebSocketSession): User? = getUser(session)
        .let(GetUserResult.Success::class::safeCast)
        ?.user

    sealed class GetUserResult {
        data class Success(val user: User) : GetUserResult()
        object EmptyPrincipal : GetUserResult()
        object UserNotFound : GetUserResult()
    }
}