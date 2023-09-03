package com.denisrebrof.springboottest.matches.gateways

import DisposableService
import com.denisrebrof.springboottest.lobby.domain.LobbyRepository
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.matches.domain.model.Match
import io.reactivex.rxjava3.core.Flowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import subscribeDefault
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class CreateMatchHandler @Autowired constructor(
    private val matchRepository: IMatchRepository,
    private val lobbyRepository: LobbyRepository
) : DisposableService() {

    private val checkLobbyTimer = 1000L

    override val handler = Flowable
        .timer(checkLobbyTimer, TimeUnit.MILLISECONDS)
        .repeat()
        .map { lobbyRepository.getUserIds() }
        .map(::getParticipantGroups)
        .onBackpressureLatest()
        .subscribeDefault(::createMatches)

    private fun getParticipantGroups(userIds: Collection<Long>): List<List<Long>> {
        val ids = userIds.toList()
        val lastIndex = userIds.size - 1
        if (lastIndex < 1)
            return listOf()

        val result = mutableListOf<List<Long>>()
        for (i in lastIndex downTo 1 step 2) {
            val user1 = ids[lastIndex - 1]
            val user2 = ids[lastIndex]
            listOf(user1, user2).let(result::add)
        }
        return result
    }

    private fun createMatches(groups: List<List<Long>>) = groups.forEach(::createMatch)

    private fun createMatch(participantIds: List<Long>) {
        val match = Match(
            id = UUID.randomUUID().toString(),
            createdTime = Date().time,
            participantIds = participantIds
        )
        participantIds.forEach(lobbyRepository::remove)
        matchRepository.add(match)
    }
}