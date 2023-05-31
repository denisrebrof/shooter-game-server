package com.denisrebrof.springboottest.fight.domain

import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.fight.domain.model.FightGame
import com.denisrebrof.springboottest.fight.gateways.model.FightPlayerStateResponse.Companion.toResponseData
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NotifyPlayerStateChangedUseCase @Autowired constructor(
    private val notificationUseCase: SendUserNotificationUseCase,
) {
    fun notify(game: FightGame, userId: Long) = with(game) {
        val playerState = playerStates[userId] ?: return@with
        val responseContent = playerState
            .toResponseData()
            .let(Json.Default::encodeToString)
            .let(NotificationContent::Data)

        game.playerStates.keys.forEach { receiverId ->
            val command = when (userId) {
                receiverId -> WSCommand.PlayerStateUpdate
                else -> WSCommand.OpponentStateUpdate
            }
            notificationUseCase.send(receiverId, command.id, responseContent)
        }
    }
}