package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.balance.data.CurrencyType
import com.denisrebrof.springboottest.balance.domain.IncreaseBalanceUseCase
import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.model.FightGame
import com.denisrebrof.springboottest.fight.domain.model.GameState
import com.denisrebrof.springboottest.fight.gateways.model.FightFinishedResponse
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FinishFightUseCase @Autowired constructor(
    private val sendUserNotificationUseCase: SendUserNotificationUseCase,
    private val increaseBalanceUseCase: IncreaseBalanceUseCase,
    private val disposeMatchUseCase: DisposeMatchUseCase,
    private val gamesRepository: FightGamesRepository
) {
    fun finish(matchId: String, winnerId: Long? = null) {
        gamesRepository.get(matchId)?.finish(winnerId) ?: return
        disposeMatchUseCase.disposeMatch(matchId)
    }

    private fun FightGame.finish(winnerId: Long? = null) {
        state = winnerId?.let(GameState::HasWinner) ?: GameState.Draw
        val isDraw = state is GameState.Draw
        playerStates.keys.forEach { playerId ->
            val isWinner = state !is GameState.Draw && playerId == winnerId
            val reward = if (isWinner) GameReward else 0L
            val response = FightFinishedResponse(true, isWinner, isDraw, reward)
            sendGameFinishedNotification(playerId, response)
        }

        if (winnerId == null)
            return

        increaseBalanceUseCase.increase(winnerId, GameReward, CurrencyType.Primary.id)
    }

    private fun sendGameFinishedNotification(userId: Long, response: FightFinishedResponse) {
        val responseContent = response
            .let { Json.encodeToString(this) }
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