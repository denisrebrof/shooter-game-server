package com.denisrebrof.springboottest.tictac.domain

import com.denisrebrof.springboottest.commands.domain.model.WSCommandId
import com.denisrebrof.springboottest.commands.gateways.WSNotificationService
import com.denisrebrof.springboottest.tictac.domain.model.GameState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.reflect.safeCast
import com.denisrebrof.springboottest.tictac.gateways.model.TicTacCellUpdateResponse as CellUpdateResponse

@Service
class TicTacMakeTurnUseCase @Autowired constructor(
    private val gameUseCase: TicTacUserGameUseCase,
    private val notificationService: WSNotificationService
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

        val gameState = TicTacWinnerDelegate
            .getWinnerId(cellStates, game.size)
            ?.let(GameState::Finished)
            ?: GameState.ActiveTurn(opponentId)

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
    ) = notificationService.send(
        userId = participantId,
        commandId = WSCommandId.TicTacCellUpdates.id,
        data = Json.encodeToString(response)
    )

    private fun sendTurnUpdate(
        participantId: Long,
        isUserTurn: Boolean
    ) = notificationService.send(
        userId = participantId,
        commandId = WSCommandId.TicTacTurnUpdates.id,
        data = isUserTurn.toString()
    )
}