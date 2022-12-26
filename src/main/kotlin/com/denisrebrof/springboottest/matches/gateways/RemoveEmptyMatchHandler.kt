package com.denisrebrof.springboottest.matches.gateways

import com.denisrebrof.springboottest.lobby.domain.LobbyRepository
import com.denisrebrof.springboottest.matches.domain.MatchRepository
import com.denisrebrof.springboottest.matches.domain.MatchRepository.MatchUpdate.UpdateType
import com.denisrebrof.springboottest.matches.domain.model.Match
import com.denisrebrof.springboottest.session.domain.WSSessionRepository
import com.denisrebrof.springboottest.session.domain.WSSessionRepository.SessionState
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.filterIsTrue
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.reflect.cast

@Service
class RemoveEmptyMatchHandler @Autowired constructor(
    private val matchRepository: MatchRepository,
    private val lobbyRepository: LobbyRepository,
    private val sessionRepository: WSSessionRepository
) : DisposableService() {

    private val createdMatchesFlow = matchRepository
        .getMatchUpdates()
        .filter { it.type == UpdateType.Created }
        .map(MatchRepository.MatchUpdate::match)

    override val handler = createdMatchesFlow
        .flatMapMaybe(::getEverybodyLeftMaybe)
        .map(Match::id)
        .onBackpressureBuffer()
        .subscribeDefault(matchRepository::remove)

    private fun getEverybodyLeftMaybe(match: Match): Maybe<Match> {
        val sessionStateFlows = match
            .participantIds
            .filterNot(lobbyRepository::isInLobby)
            .map(sessionRepository::getSessionFlow)
        val onlineStatesFlow = Flowable
            .combineLatest(sessionStateFlows, ::castToSessionStates)
            .map { it.map { state -> state is SessionState.SessionExists } }
        val everybodyLeftMaybe = onlineStatesFlow
            .map { states -> states.all { online -> !online } }
            .filterIsTrue()
            .firstElement()
        val matchFinishedMaybe = matchRepository
            .getMatchUpdates()
            .filter { update -> update.match.id == match.id }
            .map(MatchRepository.MatchUpdate::type)
            .filter(UpdateType.Removed::equals)
            .map { false }
            .firstElement()
        return everybodyLeftMaybe
            .ambWith(matchFinishedMaybe)
            .filterIsTrue()
            .map { match }
    }

    private fun castToSessionStates(input: Array<Any>) = input
        .map(SessionState::class::cast)
}