package com.denisrebrof.springboottest.session.domain

import com.denisrebrof.springboottest.session.domain.WSSessionRepository.SessionState
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import kotlin.reflect.safeCast

@Service
class HandleUserConnectionUseCase(
    private val userBySessionUseCase: UserBySessionUseCase,
    private val sessionRepository: WSSessionRepository
) {
    fun addSession(session: WebSocketSession): AddSessionResult {
        val userId = userBySessionUseCase
            .getUserNullable(session)
            ?.id
            ?: return AddSessionResult.Failure

        sessionRepository
            .getSession(userId)
            .let(SessionState.SessionExists::class::safeCast)
            ?.session
            ?.close(CloseStatus.SESSION_NOT_RELIABLE)
        sessionRepository.addSession(userId, session)
        return AddSessionResult.Success
    }

    fun removeSession(session: WebSocketSession) = userBySessionUseCase
        .getUserNullable(session)
        ?.id
        ?.let(sessionRepository::removeSession)
        ?: Unit

    enum class AddSessionResult {
        Success,
        Failure
    }
}