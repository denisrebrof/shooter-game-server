package com.denisrebrof.shooter.domain.services

import arrow.atomic.AtomicInt
import arrow.atomic.value
import com.denisrebrof.matches.domain.model.Match
import com.denisrebrof.matches.domain.services.MatchGameService
import com.denisrebrof.progression.domain.AddXpUseCase
import com.denisrebrof.shooter.domain.game.ShooterGame
import com.denisrebrof.shooter.domain.model.Finished
import com.denisrebrof.shooter.domain.model.ShooterGameSettings
import com.denisrebrof.shooter.domain.repositories.DefaultMapSettings
import com.denisrebrof.shooter.domain.repositories.DesertMapSettings
import com.denisrebrof.shooter.domain.repositories.IShooterGamePlayerStatsRepository
import com.denisrebrof.shooter.domain.repositories.TundraMapSettings
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
    private val addXpUseCase: AddXpUseCase,
    private val statsReceiver: ISimpleStatsReceiver
) : MatchGameService<ShooterGame>(), DisposableBean {

    private val clearFinishedGameDelayMs: Long = 1000L

    private var createdGamesCount: AtomicInt = AtomicInt(0)
    private var finishedGamesCount: AtomicInt = AtomicInt(0)
    private var finishedNormallyCount: AtomicInt = AtomicInt(0)

    private val saveStatsHandlers = CompositeDisposable()

    private val defaultSettings = ShooterGameSettings(
        defaultHp = 100,
        respawnDelay = 3000L,
        prepareDelay = 5000L,
        gameDuration = 1000L * 600,
        mapSettings = DefaultMapSettings,
        botSettings = ShooterGameSettings.BotSettings(
            defaultWeaponId = 1L,
            fillWithBotsToTeamSize = 0,
        ),
        completeDelay = 5000L
    )

    init {
        statsReceiver.setPropertyString("Current Games Count") { gamesMap.size.toString() }
        statsReceiver.setPropertyString("Created Games Count") { createdGamesCount.toString() }
        statsReceiver.setPropertyString("Finished Games Count") { finishedGamesCount.toString() }
    }

    override fun onMatchFinished(match: Match) {
        get(match.id)?.state?.let { state ->
            finishedGamesCount.value += 1
            if (state is Finished) {
                finishedNormallyCount.value += 1
                savePlayerStats(state).let(saveStatsHandlers::add)
                statsReceiver.addLog("Finished match for ${match.participants.size} players with id ${match.id}")
            } else {
                statsReceiver.addLog("Aborted match for ${match.participants.size} players with id ${match.id}")
            }
        }
        super.onMatchFinished(match)
    }

    override fun createGame(
        match: Match
    ): ShooterGame {
        createdGamesCount.value += 1
        statsReceiver.addLog("Create match for ${match.participants.size} players with id ${match.id}")
        val mapSettings = when(match.mapId) {
            0 -> DefaultMapSettings
            1 -> TundraMapSettings
            2 -> DesertMapSettings
            else -> DefaultMapSettings //TODO
        }
        val settings = defaultSettings.copy(
            mapSettings = mapSettings,
            botSettings = defaultSettings.botSettings.copy(
                fillWithBotsToTeamSize = match.minParticipants / 2
//                fillWithBotsToTeamSize = 0
            )
        )
        val playerIds = match.participants.toList()
        val game = createGameUseCase.create(playerIds, settings)
        createClearFinishedMatchHandler(match.id, game)
        return game
    }

    override fun destroy() = saveStatsHandlers.clear()

    private fun savePlayerStats(state: Finished) = Flowable
        .fromIterable(state.finishedPlayers.entries)
        .subscribeDefault { (id, data) ->
            //TODO: Move out to separate use case
            val won = data.team == state.winnerTeam
            val winMul = if (won) 2 else 1
            val xpAmount = data.kills.times(5).plus(10).times(winMul)
            addXpUseCase.addXp(id, xpAmount)
            playerStatsRepository.handleMatchResults(
                userId = id,
                won = won,
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