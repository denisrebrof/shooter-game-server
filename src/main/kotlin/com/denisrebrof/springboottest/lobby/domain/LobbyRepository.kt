package com.denisrebrof.springboottest.lobby.domain

import com.denisrebrof.springboottest.lobby.domain.LobbyRepository.LobbyUpdate.LobbyUpdateType
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.stereotype.Service
import java.util.*

@Service
class LobbyRepository {
    private val entryTimeToUserId = Collections.synchronizedMap(sortedMapOf<Long, Long>())
    private val userIdToEntryTime = Collections.synchronizedMap(mutableMapOf<Long, Long>())

    private val updates = PublishProcessor.create<LobbyUpdate>()

    fun getUpdates(): Flowable<LobbyUpdate> = updates

    fun getUserIds(): Collection<Long> = entryTimeToUserId.values

    fun isInLobby(userId: Long) = userIdToEntryTime.containsKey(userId)

    fun add(userId: Long) {
        val entryTime = Date().time

        //Handle re-join
        val prevEntryTime = userIdToEntryTime[userId]
        if (prevEntryTime != null) {
            entryTimeToUserId.remove(prevEntryTime)
        }

        entryTimeToUserId[entryTime] = userId
        userIdToEntryTime[userId] = entryTime
        LobbyUpdate(userId, LobbyUpdateType.Join).let(updates::onNext)
    }

    fun remove(userId: Long) {
        val entryTime = userIdToEntryTime[userId] ?: return
        userIdToEntryTime.remove(userId)
        entryTimeToUserId.remove(entryTime)
        LobbyUpdate(userId, LobbyUpdateType.Left).let(updates::onNext)
    }

    data class LobbyUpdate(
        val userId: Long,
        val type: LobbyUpdateType
    ) {
        enum class LobbyUpdateType {
            Join,
            Left
        }
    }


}