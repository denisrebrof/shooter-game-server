package com.denisrebrof.springboottest.tictac.domain

import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.tictac.domain.model.GameState
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.reflect.safeCast
import com.denisrebrof.springboottest.tictac.gateways.model.TicTacCellUpdateResponse as CellUpdateResponse

@Service
class TicTacMakeTurnUseCase @Autowired constructor(
    private val gameUseCase: TicTacUserGameUseCase,
    private val sendNotificationUseCase: SendUserNotificationUseCase
) {
    fun makeTurn(userId: Long, cellIndex: Int): Boolean {
        var game = gameUseCase
            .get(userId)
            ?: return false

        val isCurrentPlayerTurn = game
            .state
            .let(GameState.ActiveTurn::class::safeCast)
            ?.turnUserId
            ?.let(userId::equals)

        if (isCurrentPlayerTurn != true)
            return false

        val currentCellValue = game
            .userIdToCell
            .getOrNull(cellIndex)
            ?: return false

        if (currentCellValue != 0L)
            return false

        val cellStates = game.userIdToCell.toMutableList()
        cellStates[cellIndex] = userId

        val participants = game.participantIds

        participants
            .associateWith { id -> CellUpdateResponse(cellIndex, userId == id) }
            .forEach(::sendCellUpdate)

        val opponentId = participants
            .filterNot(userId::equals)
            .firstOrNull()
            ?: return false

        val winnerId = TicTacWinnerDelegate.getWinnerId(cellStates, game.size)

        val gameState = when {
            winnerId != null -> GameState.HasWinner(winnerId)
            cellStates.none(0L::equals) -> GameState.Draw
            else -> GameState.ActiveTurn(opponentId)
        }

        participants
            .associateWith(opponentId::equals)
            .forEach(::sendTurnUpdate)

        game = game.copy(userIdToCell = cellStates, state = gameState)
        gameUseCase.set(userId, game)
        return true
    }

    private fun sendCellUpdate(
        participantId: Long,
        response: CellUpdateResponse
    ) = sendNotificationUseCase.send(
        userId = participantId,
        commandId = WSCommand.TicTacCellUpdates.id,
        content = Json.encodeToString(response).let(NotificationContent::Data)
    )

    private fun sendTurnUpdate(
        participantId: Long,
        isUserTurn: Boolean
    ) = sendNotificationUseCase.send(
        userId = participantId,
        commandId = WSCommand.TicTacTurnUpdates.id,
        content = isUserTurn.toString().let(NotificationContent::Data)
    )
}