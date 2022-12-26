package com.denisrebrof.springboottest.session.domain

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.*

@Service
class WSSessionRepository {

    private val sessions = Collections.synchronizedMap(mutableMapOf<Long, WebSocketSession>())
    private val sessionStateUpdates = PublishProcessor.create<SessionStateUpdate>()

    internal fun addSession(userId: Long, session: WebSocketSession) {
        sessions[userId] = session
        val sessionState = SessionState.SessionExists(session)
        SessionStateUpdate(userId, sessionState).let(sessionStateUpdates::onNext)
    }

    internal fun removeSession(userId: Long) {
        sessions.remove(userId)
        SessionStateUpdate(userId, SessionState.Disconnected).let(sessionStateUpdates::onNext)
    }

    fun getSession(userId: Long): SessionState = sessions[userId]
        ?.let(SessionState::SessionExists)
        ?: SessionState.Disconnected

    fun getSessionFlow(userId: Long): Flowable<SessionState> = sessionStateUpdates
        .filter { update -> update.userId == userId }
        .map(SessionStateUpdate::state)
        .distinctUntilChanged()
        .startWithItem(getSession(userId))

    fun getSessionEventsFlow(): Flowable<SessionStateUpdate> = sessionStateUpdates

    data class SessionStateUpdate(
        val userId: Long,
        val state: SessionState
    )

    sealed class SessionState {
        data class SessionExists(val session: WebSocketSession) : SessionState()
        object Disconnected : SessionState()
    }
}