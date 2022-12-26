package com.denisrebrof.springboottest.matches.domain

import com.denisrebrof.springboottest.matches.domain.MatchRepository.MatchUpdate.UpdateType
import com.denisrebrof.springboottest.matches.domain.model.Match
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.stereotype.Service
import java.util.*

@Service
class MatchRepository {

    private val matches = Collections.synchronizedMap(mutableMapOf<String, Match>())
    private val userIdToMatchId = Collections.synchronizedMap(mutableMapOf<Long, String>())

    private val updates = PublishProcessor.create<MatchUpdate>()

    fun get(matchId: String) = matches[matchId]

    fun add(match: Match) {
        if (matches.containsKey(match.id))
            return

        matches[match.id] = match
        match.participantIds.forEach { userIdToMatchId[it] = match.id }
        MatchUpdate(match, UpdateType.Created).let(updates::onNext)
    }

    fun remove(matchId: String) {
        val match = matches.remove(matchId) ?: return
        match.participantIds.forEach { userIdToMatchId.remove(it) }
        MatchUpdate(match, UpdateType.Removed).let(updates::onNext)
    }

    fun getMatchUpdates(): Flowable<MatchUpdate> = updates

    fun getMatchIdByUserId(userID: Long) = userIdToMatchId[userID]

    data class MatchUpdate(
        val match: Match,
        val type: UpdateType
    ) {
        enum class UpdateType {
            Created,
            Removed
        }
    }
}