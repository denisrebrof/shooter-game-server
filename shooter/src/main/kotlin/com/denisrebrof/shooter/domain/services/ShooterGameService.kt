package com.denisrebrof.shooter.domain.services

import com.denisrebrof.matches.domain.model.Match
import com.denisrebrof.matches.domain.services.MatchGameService
import com.denisrebrof.shooter.domain.ShooterGame
import com.denisrebrof.shooter.domain.model.Finished
import com.denisrebrof.shooter.domain.repositories.IShooterGamePlayerStatsRepository
import com.denisrebrof.shooter.domain.usecases.CreateShooterGameUseCase
import com.denisrebrof.simplestats.domain.ISimpleStatsReceiver
import com.denisrebrof.simplestats.domain.setPropertyString
import com.denisrebrof.utils.subscribeDefault
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import com.denisrebrof.shooter.domain.model.ShooterGameActions as Actions

@Service
class ShooterGameService @Autowired constructor(
    private val createGameUseCase: CreateShooterGameUseCase,
    private val playerStatsRepository: IShooterGamePlayerStatsRepository,
    private val statsReceiver: ISimpleStatsReceiver
) : MatchGameService<ShooterGame>(), DisposableBean {

    private val clearFinishedGameDelayMs: Long = 1000L

    private var createdGamesCount: Int = 0
    private var finishedGamesCount: Int = 0
    private var finishedNormallyCount: Int = 0

    private val saveStatsHandlers = CompositeDisposable()

    init {
        statsReceiver.setPropertyString("Current Games Count") { gamesMap.size.toString() }
        statsReceiver.setPropertyString("Created Games Count") { createdGamesCount.toString() }
        statsReceiver.setPropertyString("Finished Games Count") { finishedGamesCount.toString() }
    }

    override fun onMatchFinished(match: Match) {
        finishedGamesCount++
        get(match.id)?.state?.let { state ->
            if (state is Finished) {
                finishedNormallyCount += 1
                savePlayerStats(state).let(saveStatsHandlers::add)
                statsReceiver.addLog("Finished match for ${match.participants.size} players with id ${match.id}")
            } else {
                statsReceiver.addLog("Aborted match for ${match.participants.size} players with id ${match.id}")
            }
        }
        super.onMatchFinished(match)
    }

    override fun createGame(match: Match): ShooterGame {
        createdGamesCount += 1
        statsReceiver.addLog("Create match for ${match.participants.size} players with id ${match.id}")
        return match
            .participants
            .toList()
            .let(createGameUseCase::create)
            .also { game -> createClearFinishedMatchHandler(match.id, game) }
    }

    override fun destroy() = saveStatsHandlers.clear()

    private fun savePlayerStats(state: Finished) = Flowable
        .fromIterable(state.finishedPlayers.entries)
        .subscribeDefault { (id, data) ->
            playerStatsRepository.handleMatchResults(
                userId = id,
                won = data.team == state.winnerTeam,
                kills = data.kills,
                death = data.death
            )
        }

    private fun createClearFinishedMatchHandler(matchId: String, game: ShooterGame) {
        game
            .actions
            .filter(Actions.LifecycleCompleted::equals)
            .delay(clearFinishedGameDelayMs, TimeUnit.MILLISECONDS)
            .subscribeDefault { removeGame(matchId) }
            .let(game::add)
    }
}