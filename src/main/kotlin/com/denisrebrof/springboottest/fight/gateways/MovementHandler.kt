package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.fight.domain.FightGamesRepository
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository.GameUpdate
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository.GameUpdateType
import com.denisrebrof.springboottest.fight.domain.NotifyPlayerStateChangedUseCase
import com.denisrebrof.springboottest.fight.domain.model.*
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class MovementHandler @Autowired constructor(
    private val notifyPlayerStateChangedUseCase: NotifyPlayerStateChangedUseCase,
    gamesRepository: FightGamesRepository,
) : DisposableService() {

    companion object {
        private const val PLAYER_SPEED_PER_SECOND = 2f
        private const val UPDATE_DELAY_MS = 50L
        private const val PLAYER_SPEED = PLAYER_SPEED_PER_SECOND * UPDATE_DELAY_MS / 1000L

        private const val PLAYER_RADIUS = 1f
    }

    override val handler: Disposable = gamesRepository
        .getUpdates()
        .filter { update -> update.type != GameUpdateType.Removed }
        .map(GameUpdate::game)
        .subscribeDefault(::setupMovementHandler)

    private fun setupMovementHandler(game: FightGame) = Flowable
        .timer(UPDATE_DELAY_MS, TimeUnit.MILLISECONDS)
        .repeat()
        .filter { game.state == GameState.Playing }
        .subscribeDefault { updateMovement(game) }
        .let(game::doUntilFinish)

    private fun updateMovement(game: FightGame) = game.apply {
        val occupiedRanges = playerStates
            .mapValues { (_, playerState) -> playerState.state.position }
            .mapValues { (_, position) -> position - PLAYER_RADIUS..position + PLAYER_RADIUS }

        playerStates.forEach { (playerId, state) ->
            val speed = state.speed

            if (speed == 0f)
                return@forEach

            val otherPlayerRanges = occupiedRanges.minus(playerId).values
            val newPosition = state.state.position + speed
            if (otherPlayerRanges.any { it.contains(newPosition) })
                return@forEach

            state.state.position = newPosition
            notifyPlayerStateChangedUseCase.notify(game, playerId)
        }
    }

    private val PlayerState.speed: Float
        get() {
            if (state !is FighterState.Fighting)
                return 0f

            return when (intents.movement) {
                MovementDirection.None -> 0f
                MovementDirection.Forward -> PLAYER_SPEED
                MovementDirection.Backward -> -PLAYER_SPEED
            }
        }
}