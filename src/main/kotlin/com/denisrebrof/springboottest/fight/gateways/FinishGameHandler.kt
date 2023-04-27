package com.denisrebrof.springboottest.fight.gateways

import com.denisrebrof.springboottest.balance.data.CurrencyType
import com.denisrebrof.springboottest.balance.domain.IncreaseBalanceUseCase
import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.model.FightGame
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository
import com.denisrebrof.springboottest.fight.domain.FightGamesRepository.*
import com.denisrebrof.springboottest.fight.domain.model.GameState
import com.denisrebrof.springboottest.fight.gateways.model.FightFinishedResponse
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import com.denisrebrof.springboottest.utils.DisposableService
import com.denisrebrof.springboottest.utils.subscribeDefault
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.reflect.safeCast

@Service
class FinishGameHandler @Autowired constructor(
    gamesRepository: FightGamesRepository,
    private val sendUserNotificationUseCase: SendUserNotificationUseCase,
    private val increaseBalanceUseCase: IncreaseBalanceUseCase,
) : DisposableService() {

    override val handler: Disposable = gamesRepository
        .getUpdates()
        .filter { it.type == GameUpdateType.Updated }
        .map(GameUpdate::game)
        .onBackpressureBuffer()
        .subscribeDefault(::notifyGameFinished)

    private fun notifyGameFinished(game: FightGame) = with(game) {
        if (!state.finished)
            return

        val winnerId = state.let(GameState.HasWinner::class::safeCast)?.winnerId
        val isDraw = state is GameState.Draw

        game.playerStates.keys.forEach { playerId ->
            val isWinner = !isDraw && playerId == winnerId
            val reward = if (isWinner) GameReward else 0L
            val response = FightFinishedResponse(true, isWinner, isDraw, reward)
            sendGameFinishedNotification(playerId, response)
        }

        if (winnerId == null)
            return@with

        increaseBalanceUseCase.increase(winnerId, GameReward, CurrencyType.Primary.id)
    }

    private fun sendGameFinishedNotification(userId: Long, response: FightFinishedResponse) {
        val responseContent = response
            .let{ Json.encodeToString(this) }
            .let(NotificationContent::Data)
        sendUserNotificationUseCase.send(
            userId = userId,
            commandId = WSCommand.FightFinished.id,
            content = responseContent
        )
    }

    companion object {
        private const val GameReward = 10L
    }
}