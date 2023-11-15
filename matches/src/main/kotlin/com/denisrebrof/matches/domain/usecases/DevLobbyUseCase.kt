package com.denisrebrof.matches.domain.usecases

import org.springframework.stereotype.Service
import java.util.*

@Service
class DevLobbyUseCase {
    private val devUserIds = Collections.synchronizedList(mutableListOf<Long>())

    fun add(userId: Long) = devUserIds.add(userId)

    fun remove(userId: Long) = devUserIds.remove(userId)

    fun isDev(userId: Long) = devUserIds.contains(userId)
}