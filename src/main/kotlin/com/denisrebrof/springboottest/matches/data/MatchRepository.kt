package com.denisrebrof.springboottest.matches.data

import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate.UpdateType
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class MatchRepository @Autowired constructor() : IMatchRepository {

    private val matches = Collections.synchronizedMap(mutableMapOf<String, Match>())
    private val userIdToMatchId = Collections.synchronizedMap(mutableMapOf<Long, String>())

    private val updates = PublishProcessor.create<MatchUpdate>()

    override fun get(matchId: String) = matches[matchId]

    override fun add(match: Match) {
        if (matches.containsKey(match.id))
            return

        matches[match.id] = match
        match.participantIds.forEach { userIdToMatchId[it] = match.id }
        MatchUpdate(match, UpdateType.Created).let(updates::onNext)
    }

    override fun remove(matchId: String) {
        val match = matches.remove(matchId) ?: return
        match.participantIds.forEach { userIdToMatchId.remove(it) }
        MatchUpdate(match, UpdateType.Removed).let(updates::onNext)
    }

    override fun getMatchUpdates(): Flowable<MatchUpdate> = updates

    override fun getMatchIdByUserId(userID: Long) = userIdToMatchId[userID]


}