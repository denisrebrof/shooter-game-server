package com.denisrebrof.user.domain.repositories

import io.reactivex.rxjava3.core.Flowable

interface IWSUserSessionEventsRepository {

    fun getSessionEventsFlow(): Flowable<UserSessionEvent>

    data class UserSessionEvent(
        val userId: Long,
        val sessionId: String,
        val type: UserSessionEventType
    )

    enum class UserSessionEventType {
        Connected,
        Disconnected
    }
}