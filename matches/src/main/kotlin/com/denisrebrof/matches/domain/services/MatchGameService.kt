package com.denisrebrof.matches.domain.services

import com.denisrebrof.matches.domain.model.IParticipantsHandler
import com.denisrebrof.matches.domain.model.Match
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy

abstract class MatchGameService<TGame> : IMatchServiceListener where TGame : IParticipantsHandler, TGame : Disposable {

    @Autowired
    @Lazy
    private lateinit var matchService: MatchService

    private val matchIdToGame = mutableMapOf<String, TGame>()

    protected val gamesMap: Map<String, TGame>
        get() = matchIdToGame

    abstract fun createGame(match: Match): TGame

    fun get(matchId: String) = matchIdToGame[matchId]

    override fun onMatchStarted(match: Match) = createGame(match).let { matchIdToGame[match.id] = it }

    override fun onLeft(match: Match, vararg participantIds: Long) = matchIdToGame[match.id]
        ?.removePlayers(*participantIds)
        ?: Unit

    override fun onJoined(match: Match, vararg participantIds: Long) = matchIdToGame[match.id]
        ?.addPlayers(*participantIds)
        ?: Unit

    override fun onMatchFinished(match: Match) = matchIdToGame
        .remove(match.id)
        ?.dispose()
        ?: Unit

    protected fun removeGame(matchId: String) = matchService.remove(matchId)
}