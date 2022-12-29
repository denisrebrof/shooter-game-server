package com.denisrebrof.springboottest.session.domain

import io.reactivex.rxjava3.core.Flowable
import org.springframework.web.socket.WebSocketSession

interface IWSConnectedSessionRepository {

    fun addSession(session: WebSocketSession)
    fun removeSession(sessionId: String)

    fun getSession(sessionId: String): SessionState
    fun getSessionFlow(sessionId: String): Flowable<SessionState>

    sealed class SessionState {
        data class Exists(val session: WebSocketSession) : SessionState()
        object NotFound : SessionState()
    }
}
