package com.denisrebrof.springboottest.tictac.gateways

import com.denisrebrof.springboottest.commands.domain.model.WSCommandId
import com.denisrebrof.springboottest.commands.gateways.WSEmptyRequestHandler
import com.denisrebrof.springboottest.commands.gateways.WSRequestHandler.ResponseState.NoResponse
import com.denisrebrof.springboottest.tictac.domain.TicTacUserGameUseCase
import com.denisrebrof.springboottest.tictac.domain.model.GameState
import com.denisrebrof.springboottest.tictac.domain.model.TicTacGame
import com.denisrebrof.springboottest.tictac.gateways.model.TicTacGameStateResponse
import com.denisrebrof.springboottest.user.IUserRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.reflect.safeCast

@Service
class TicTacGameStateRequestHandler @Autowired constructor(
    private val gameUseCase: TicTacUserGameUseCase,
    private val userRepository: IUserRepository
) : WSEmptyRequestHandler(WSCommandId.TicTacState.id) {

    private val mapper = CellCodeMapper()

    override fun handleMessage(userId: Long): ResponseState {
        val game = gameUseCase.get(userId) ?: return NoResponse
        val activeTurnPlayerId = game
            .state
            .let(GameState.ActiveTurn::class::safeCast)
            ?.turnUserId

        val opponentId = game
            .participantIds
            .filterNot(userId::equals)
            .firstOrNull()
            ?: return NoResponse

        userRepository
            .findUserById(opponentId)
            .username

        val winnerId = game.state.let(GameState.Finished::class::safeCast)?.winnerId
        val response = TicTacGameStateResponse(
            cellStates = getCellStates(game, userId),
            isPlayerTurn = activeTurnPlayerId?.let(userId::equals) ?: false,
            isWinner = winnerId?.let(userId::equals) ?: false,
            gridSize = game.size,
            gameState = game.state.id,
            opponentNick = userRepository.findUserById(opponentId).username
        )
        return response
            .let(Json::encodeToString)
            .let(ResponseState::CreatedResponse)
    }

    @Synchronized
    private fun getCellStates(game: TicTacGame, userId: Long): List<Int> {
        mapper.userId = userId
        return game.userIdToCell.map(mapper::getCellCode)
    }
}

private class CellCodeMapper(var userId: Long = 0L) {
    fun getCellCode(cellParticipantId: Long) = when (cellParticipantId) {
        0L -> 0
        userId -> 1
        else -> 2
    }
}