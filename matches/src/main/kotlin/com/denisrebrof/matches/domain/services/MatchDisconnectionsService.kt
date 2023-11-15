package com.denisrebrof.matches.domain.services

import com.denisrebrof.matches.domain.model.Match
import com.denisrebrof.user.domain.repositories.IWSUserSessionRepository
import com.denisrebrof.user.domain.repositories.IWSUserSessionRepository.UserSessionState
import com.denisrebrof.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.util.*

@Service
class MatchDisconnectionsService @Autowired constructor(
    private val sessionRepository: IWSUserSessionRepository
) : IMatchServiceListener by EmptyMatchServiceListener {

    @Autowired
    @Lazy
    private lateinit var matchService: MatchService

    private val disconnectionHandlers = Collections.synchronizedMap(mutableMapOf<Long, Disposable>())

    override fun onMatchStarted(match: Match) = match
        .participants
        .forEach { participantId -> setupDisconnectionHandler(participantId, match.id) }

    override fun onMatchFinished(match: Match) = match
        .participants
        .toList()
        .let(::disposeDisconnectionHandlers)

    override fun onJoined(match: Match, vararg participantIds: Long) = participantIds
        .forEach { participantId -> setupDisconnectionHandler(participantId, match.id) }

    override fun onLeft(match: Match, vararg participantIds: Long) = participantIds
        .toList()
        .let(::disposeDisconnectionHandlers)

    private fun disposeDisconnectionHandlers(participantIds: List<Long>) = participantIds
        .mapNotNull(disconnectionHandlers::get)
        .forEach(Disposable::dispose)

    private fun setupDisconnectionHandler(userId: Long, matchId: String) {
        disconnectionHandlers[userId]?.dispose()
        disconnectionHandlers[userId] = createDisconnectionHandler(userId, matchId)
    }

    private fun createDisconnectionHandler(userId: Long, matchId: String) = sessionRepository
        .getSessionFlow(userId)
        .filter(UserSessionState.NotFound::class::isInstance)
        .firstElement()
        .subscribeDefault { matchService.removeUser(matchId, userId) }
}