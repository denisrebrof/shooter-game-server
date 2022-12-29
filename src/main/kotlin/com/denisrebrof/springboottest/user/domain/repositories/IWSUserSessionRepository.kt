package com.denisrebrof.springboottest.user.domain.repositories

import io.reactivex.rxjava3.core.Flowable
import org.springframework.web.socket.WebSocketSession

interface IWSUserSessionRepository {

    fun getUserSession(userId: Long): UserSessionState
    fun getSessionFlow(userId: Long): Flowable<UserSessionState>

    fun removeUserSession(userId: Long)

    sealed class UserSessionState {
        data class Exists(val session: WebSocketSession) : UserSessionState()
        object NotFound : UserSessionState()
    }
}