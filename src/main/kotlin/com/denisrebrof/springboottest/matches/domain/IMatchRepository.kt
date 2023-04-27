package com.denisrebrof.springboottest.matches.domain

import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.matches.domain.model.MatchUpdate
import io.reactivex.rxjava3.core.Flowable

interface IMatchRepository {
    fun get(matchId: String): Match?
    fun add(match: Match)
    fun remove(matchId: String)
    fun getMatchUpdates(): Flowable<MatchUpdate>
    fun getMatchIdByUserId(userID: Long): String?
}