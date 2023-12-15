package com.denisrebrof.shooter.domain.usecases

import com.denisrebrof.shooter.domain.ShooterGame
import com.denisrebrof.shooter.domain.ShooterGameSettings
import com.denisrebrof.shooter.domain.model.playerIds
import com.denisrebrof.utils.subscribeDefault
import io.reactivex.rxjava3.core.Flowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CreateShooterGameUseCase @Autowired constructor(
    private val notificationsUseCase: ShooterGameNotificationsUseCase,
) {

    private val stateSyncDelayMs = 200L

    private val defaultSettings = ShooterGameSettings(
        respawnDelay = 3000L,
        prepareDelay = 5000L,
        gameDuration = 1000L * 60 * 5,
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
            val receivers = game.state.playerIds.toLongArray()
            notificationsUseCase.notifyAction(action, *receivers)
        }.let(game::add)
}