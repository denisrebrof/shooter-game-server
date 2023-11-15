package com.denisrebrof.matches.domain.services

import com.denisrebrof.lobby.domain.LobbyRepository
import com.denisrebrof.matches.domain.usecases.DevLobbyUseCase
import com.denisrebrof.utils.DisposableService
import com.denisrebrof.utils.subscribeDefault
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CreateMatchService @Autowired constructor(
    private val matchService: MatchService,
    private val lobbyRepository: LobbyRepository,
    private val devLobbyUseCase: DevLobbyUseCase
) : DisposableService() {

    private val counter = ParticipantTiersCounter(4, 16)

    private val checkLobbyTimer = 1000L

    private val devMatchParticipantsCount = 2;

    private val refreshTierTimer = 45 * 1000L

    private val refreshTierHandler = Flowable
        .timer(refreshTierTimer, TimeUnit.MILLISECONDS)
        .repeat()
        .onBackpressureLatest()
        .subscribeDefault { counter.refresh() }

    private val createMatchesHandler = Flowable
        .timer(checkLobbyTimer, TimeUnit.MILLISECONDS)
        .repeat()
        .map { lobbyRepository.getUserIds() }
        .map(::getParticipantGroups)
        .onBackpressureLatest()
        .subscribeDefault(::createMatches)

    override val handler = CompositeDisposable(createMatchesHandler, refreshTierHandler)

    private fun getParticipantGroups(userIds: Collection<Long>): List<List<Long>> {
        val ids = userIds.toMutableList()
        val devIds = ids.filter(devLobbyUseCase::isDev)
        ids.removeAll(devIds.toSet())

        val requiredParticipants = counter.preferredParticipantsCount
        if (ids.size < requiredParticipants && devIds.size < devMatchParticipantsCount)
            return listOf()

        val result = mutableListOf<List<Long>>()
        val matchParticipants = ids.chunkedFixed(requiredParticipants)
        val devMatchParticipants = devIds.chunkedFixed(devMatchParticipantsCount)
        counter.createMatches(matchParticipants.size)
        matchParticipants.forEach(result::add)
        devMatchParticipants.forEach(result::add)
        return result
    }

    private fun createMatches(groups: List<List<Long>>) = groups.forEach(::createMatch)

    private fun createMatch(participantIds: List<Long>) {
        participantIds.forEach(lobbyRepository::remove)
        participantIds.toSet().let(matchService::create)
    }

    private fun <T> Iterable<T>.chunkedFixed(size: Int): List<List<T>> = chunked(size).filter { it.size == size }

    private class ParticipantTiersCounter(minParticipants: Int, maxParticipants: Int) {

        private var currentTierIndex = 0

        private var createdTiersCount = 0

        private val participantCountTiers = (minParticipants..maxParticipants step 2).toList()

        val preferredParticipantsCount
            get() = participantCountTiers[currentTierIndex]


        fun createMatches(matchesCount: Int) {
            createdTiersCount += matchesCount
        }

        fun refresh() {
            currentTierIndex = when {
                createdTiersCount < 1 -> (currentTierIndex - 1).coerceAtLeast(0)
                createdTiersCount > 1 -> (currentTierIndex + 1).coerceAtMost(participantCountTiers.lastIndex)
                else -> currentTierIndex
            }
            createdTiersCount = 0
        }
    }
}