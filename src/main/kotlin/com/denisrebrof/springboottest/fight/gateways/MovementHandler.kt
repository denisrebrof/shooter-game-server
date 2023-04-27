package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.fight.domain.FightGamesRepository
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository.GameUpdate
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository.GameUpdateType
import com.denisrebrof.springboottest.fight.domain.UpdateGameUseCase
import com.denisrebrof.springboottest.fight.domain.model.FighterState
import com.denisrebrof.springboottest.fight.domain.model.GameState
import com.denisrebrof.springboottest.fight.domain.model.MovementDirection
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.safeCast

@Service
class MovementHandler @Autowired constructor(
    private val fightGamesRepository: FightGamesRepository,
    private val updateGameUseCase: UpdateGameUseCase,
) : DisposableService() {

    companion object {
        private const val PLAYER_SPEED_PER_SECOND = 0.1f
        private const val UPDATE_DELAY_MS = 250L
        private const val PLAYER_SPEED = PLAYER_SPEED_PER_SECOND * UPDATE_DELAY_MS / 1000L

        private const val PLAYER_RADIUS = 0.2f
    }

    private val movementHandlers = Collections.synchronizedMap(mutableMapOf<String, Disposable>())

    override val handler: Disposable
        get() = fightGamesRepository
            .getUpdates()
            .subscribeDefault(::onGameUpdate)

    private fun onGameUpdate(update: GameUpdate) = with(update) {
        val fightPlaying = game.state == GameState.Playing && type != GameUpdateType.Removed
        when {
            fightPlaying -> addMovementHandler(matchId)
            else -> removeMovementHandler(matchId)
        }
    }

    private fun removeMovementHandler(matchId: String) {
        movementHandlers.remove(matchId)?.dispose()
    }

    private fun addMovementHandler(matchId: String) {
        if (movementHandlers.containsKey(matchId))
            return

        val handler = createMovementHandler(matchId)
        movementHandlers[matchId] = handler
    }

    private fun createMovementHandler(matchId: String): Disposable = Flowable
        .timer(UPDATE_DELAY_MS, TimeUnit.MILLISECONDS)
        .repeat()
        .subscribeDefault { updateMovement(matchId) }

    private fun updateMovement(matchId: String) = updateGameUseCase.update(matchId) {
        val states = playerStates.toMutableMap()
        val occupiedRanges = playerStates
            .mapValues { (_, playerState) -> playerState.state.position }
            .mapValues { (_, position) -> position - PLAYER_RADIUS..position + PLAYER_RADIUS }
        playerStates.forEach { (playerId, state) ->
            val fightingState = state.state
                .let(FighterState.Fighting::class::safeCast)
                ?: return@forEach

            val speed = when (state.intents.movement) {
                MovementDirection.None -> return@forEach
                MovementDirection.Forward -> PLAYER_SPEED
                MovementDirection.Backward -> -PLAYER_SPEED
            }

            val otherPlayerRanges = occupiedRanges.minus(playerId).values
            val newPosition = fightingState.position + speed
            if (otherPlayerRanges.any { it.contains(newPosition) })
                return@forEach

            val movedState = fightingState.copy(position = newPosition)
            states[playerId] = state.copy(state = movedState)
        }
        return@update this.copy(playerStates = states)
    }
}