package com.denisrebrof.shooter.domain.services

import com.denisrebrof.matches.domain.model.Match
import com.denisrebrof.matches.domain.services.MatchGameService
import com.denisrebrof.shooter.domain.ShooterGame
import com.denisrebrof.shooter.domain.usecases.CreateShooterGameUseCase
import com.denisrebrof.utils.subscribeDefault
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import com.denisrebrof.shooter.domain.model.ShooterGameActions as Actions

@Service
class ShooterGameService @Autowired constructor(
    private val createGameUseCase: CreateShooterGameUseCase
) : MatchGameService<ShooterGame>() {

    private val clearFinishedGameDelayMs: Long = 1000L

    override fun createGame(match: Match): ShooterGame = match
        .participants
        .toList()
        .let(createGameUseCase::create)
        .also { game -> createClearFinishedMatchHandler(match.id, game) }

    private fun createClearFinishedMatchHandler(matchId: String, game: ShooterGame) {
        game
            .actions
            .filter(Actions.LifecycleCompleted::equals)
            .delay(clearFinishedGameDelayMs, TimeUnit.MILLISECONDS)
            .subscribeDefault { removeGame(matchId) }
            .let(game::add)
    }
}