package com.denisrebrof.matches.domain.services

import com.denisrebrof.lobby.domain.LobbyRepository
import com.denisrebrof.matches.domain.model.Match
import com.denisrebrof.matches.domain.services.MatchService.Companion.MAX_PARTICIPANTS
import com.denisrebrof.utils.DisposableService
import com.denisrebrof.utils.another
import com.denisrebrof.utils.subscribeDefault
import io.reactivex.rxjava3.core.Flowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class FillMatchesService @Autowired constructor(
    private val matchService: MatchService,
    private val lobbyRepository: LobbyRepository
) : DisposableService() {

    private val checkLobbyTimer = 1000L

    override val handler = Flowable
        .timer(checkLobbyTimer, TimeUnit.MILLISECONDS)
        .repeat()
        .map { lobbyRepository.getUserIds() }
        .filter(Collection<Long>::isNotEmpty)
        .onBackpressureLatest()
        .subscribeDefault(::createMatches)

    private fun createMatches(userIds: Collection<Long>) {
        val iterator = userIds.iterator()
        val joinStates = matchService
            .getMatches()
            .map(::MatchJoinState)
            .filter(MatchJoinState::joinAvailable)
            .sortedBy(MatchJoinState::availableSlots)

        for (joinState in joinStates) {
            if (!iterator.hasNext())
                return

            val usersToAdd = iterator
                .another(joinState.availableSlots)
                .toLongArray()

            matchService.addUsers(joinState.matchId, *usersToAdd)
            usersToAdd.forEach(lobbyRepository::remove)
        }

        while (iterator.hasNext())
            iterator
                .another(MAX_PARTICIPANTS)
                .let(::createMatch)
    }

    private fun createMatch(participantIds: List<Long>) {
//        if (participantIds.size < 2)
//            return

        participantIds.forEach(lobbyRepository::remove)
        participantIds.toSet().let(matchService::create)
    }

    private class MatchJoinState(match: Match) {
        val matchId = match.id
        val availableSlots = MAX_PARTICIPANTS
            .minus(match.participants.size)
            .coerceAtLeast(0)
        val joinAvailable = availableSlots > 0
    }
}