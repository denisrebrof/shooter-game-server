package com.denisrebrof.commands.domain

import com.denisrebrof.commands.domain.IWSConnectedSessionRepository.SessionState
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.*

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

@Service
class WSConnectedSessionRepository : IWSConnectedSessionRepository {

    private val sessions = Collections.synchronizedMap(mutableMapOf<String, WebSocketSession>())
    private val sessionStateUpdates = PublishProcessor.create<SessionStateUpdate>()

    override fun addSession(session: WebSocketSession) {
        sessions[session.id] = session
        val sessionState = SessionState.Exists(session)
        SessionStateUpdate(session.id, sessionState).let(sessionStateUpdates::onNext)
    }

    override fun removeSession(sessionId: String) {
        sessions.remove(sessionId)
        SessionStateUpdate(sessionId, SessionState.NotFound).let(sessionStateUpdates::onNext)
    }

    override fun getSession(sessionId: String): SessionState = sessions[sessionId]
        ?.let(SessionState::Exists)
        ?: SessionState.NotFound

    override fun getSessionFlow(sessionId: String): Flowable<SessionState> {
        val currentState = getSession(sessionId)
        return sessionStateUpdates
            .filter { it.sessionId == sessionId }
            .map { it.state }
            .startWithItem(currentState)
    }

    private data class SessionStateUpdate(
        val sessionId: String,
        val state: SessionState
    )
}