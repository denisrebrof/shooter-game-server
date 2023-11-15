package com.denisrebrof.user.data

import com.denisrebrof.commands.domain.IWSConnectedSessionRepository.SessionState
import com.denisrebrof.commands.domain.WSConnectedSessionRepository
import com.denisrebrof.user.domain.repositories.IWSUserSessionEventsRepository
import com.denisrebrof.user.domain.repositories.IWSUserSessionEventsRepository.UserSessionEvent
import com.denisrebrof.user.domain.repositories.IWSUserSessionEventsRepository.UserSessionEventType
import com.denisrebrof.user.domain.repositories.IWSUserSessionMappingRepository
import com.denisrebrof.user.domain.repositories.IWSUserSessionRepository
import com.denisrebrof.user.domain.repositories.IWSUserSessionRepository.UserSessionState
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.reflect.safeCast

@Service
class WSUserSessionRepository @Autowired constructor(
    private val connectedSessionRepository: WSConnectedSessionRepository
) : IWSUserSessionRepository, IWSUserSessionMappingRepository, IWSUserSessionEventsRepository {

    private val userIdToSessionId = Collections.synchronizedMap(mutableMapOf<Long, String>())
    private val sessionIdToUserId = Collections.synchronizedMap(mutableMapOf<String, Long>())

    private val userSessionEvents = PublishProcessor.create<UserSessionEvent>()

    override fun getMapping(sessionId: String): Long? = sessionIdToUserId[sessionId]

    override fun addMapping(userId: Long, sessionId: String) {
        userIdToSessionId[userId] = sessionId
        sessionIdToUserId[sessionId] = userId
        UserSessionEvent(userId, sessionId, UserSessionEventType.Connected).let(userSessionEvents::onNext)
    }

    override fun removeMapping(sessionId: String) {
        val userId = sessionIdToUserId.remove(sessionId) ?: return
        userIdToSessionId.remove(userId)
        UserSessionEvent(userId, sessionId, UserSessionEventType.Disconnected).let(userSessionEvents::onNext)
    }

    override fun getSessionEventsFlow(): Flowable<UserSessionEvent> = userSessionEvents

    override fun removeUserSession(userId: Long) {
        val sessionId = userIdToSessionId.remove(userId) ?: return
        sessionIdToUserId.remove(sessionId)
        UserSessionEvent(userId, sessionId, UserSessionEventType.Disconnected).let(userSessionEvents::onNext)
    }

    override fun getUserSession(userId: Long): UserSessionState = userIdToSessionId[userId]
        ?.let(::getSessionNullable)
        ?.let(UserSessionState::Exists)
        ?: UserSessionState.NotFound

    override fun getSessionFlow(userId: Long): Flowable<UserSessionState> {
        val currentState = getUserSession(userId)
        return userSessionEvents
            .filter { it.userId == userId }
            .switchMap(::getSessionState)
            .startWithItem(currentState)
    }

    private fun getSessionState(event: UserSessionEvent): Flowable<UserSessionState> = connectedSessionRepository
        .getSessionFlow(event.sessionId)
        .map(::getUserSessionState)

    private fun getUserSessionState(sessionState: SessionState): UserSessionState = when (sessionState) {
        is SessionState.Exists -> UserSessionState.Exists(sessionState.session)
        SessionState.NotFound -> UserSessionState.NotFound
    }

    private fun getSessionNullable(sessionId: String) = connectedSessionRepository
        .getSession(sessionId)
        .let(SessionState.Exists::class::safeCast)
        ?.session
}