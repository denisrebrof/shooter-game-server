package com.denisrebrof.springboottest.tictac.gateways

import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.tictac.domain.TicTacGameRepository
import com.denisrebrof.springboottest.tictac.domain.TicTacGameRepository.GameUpdateType
import com.denisrebrof.springboottest.tictac.domain.model.GameState
import com.denisrebrof.springboottest.tictac.domain.model.TicTacGame
import com.denisrebrof.springboottest.tictac.gateways.model.TicTacFinishedStateResponse
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
class TicTacFinishedStateNotifier @Autowired constructor(
    gamesRepository: TicTacGameRepository,
    private val sendUserNotificationUseCase: SendUserNotificationUseCase
) : DisposableService() {
    override val handler: Disposable = gamesRepository
        .getUpdates()
        .filter { it.type == GameUpdateType.Updated }
        .map(TicTacGameRepository.GameUpdate::game)
        .onBackpressureBuffer()
        .subscribeDefault(::notifyGameFinished)

    private fun notifyGameFinished(game: TicTacGame) = with(game) {
        if (!state.finished)
            return

        val winnerId = state.let(GameState.HasWinner::class::safeCast)?.winnerId
        val isDraw = state is GameState.Draw
        game.participantIds.forEach { participantId ->
            val isWinner = !isDraw && participantId == winnerId
            val response = TicTacFinishedStateResponse(true, isWinner, isDraw)
            sendGameFinishedNotification(participantId, response)
        }
    }

    private fun sendGameFinishedNotification(userId: Long, response: TicTacFinishedStateResponse) {
        val responseContent = response
            .let(Json::encodeToString)
            .let(NotificationContent::Data)
        sendUserNotificationUseCase.send(
            userId = userId,
            commandId = WSCommand.TicTacFinished.id,
            content = responseContent
        )
    }
}