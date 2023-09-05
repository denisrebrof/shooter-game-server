package com.denisrebrof.springboottest.shooter

import ShooterGame
import ShooterGameSettings
import gameentities.Transform
import io.reactivex.rxjava3.core.Flowable
import model.playerIds
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import subscribeDefault
import java.util.concurrent.TimeUnit

@Service
class CreateShooterGameUseCase @Autowired constructor(
    private val notificationsUseCase: ShooterGameNotificationsUseCase,
) {

    private val stateSyncDelayMs = 200L

    private val defaultSettings = ShooterGameSettings(
        redTeamSpawnPos = Transform(0f, 0f, 0f, 0f),
        blueTeamSpawnPos = Transform(10f, 0f, 0f, 0f),
        respawnDelay = 3000L,
        prepareDelay = 2500L,
        gameDuration = 1000000L,
        completeDelay = 10000L
    )

    fun create(playerIds: List<Long>) = ShooterGame
        .create(playerIds, defaultSettings)
        .also(::createStateHandler)
        .also(::createActionsHandler)
        .also(ShooterGame::start)

    private fun createStateHandler(game: ShooterGame) = Flowable
        .interval(stateSyncDelayMs, TimeUnit.MILLISECONDS)
        .startWithItem(0L)
        .map { game.state }
        .subscribeDefault(notificationsUseCase::notifyStateChanged)
        .let(game::add)

    private fun createActionsHandler(game: ShooterGame) = game
        .actions
        .subscribeDefault { action ->
            val receivers = game.state.playerIds.toList()
            notificationsUseCase.notifyAction(action, receivers)
        }.let(game::add)
}