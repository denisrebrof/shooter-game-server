package com.denisrebrof.springboottest.shooter

import com.denisrebrof.springboottest.commands.domain.model.NotificationContent
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.user.domain.SendUserNotificationUseCase
import gameentities.Transform
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterGameNotificationsUseCase @Autowired constructor(
    private val sendUserNotificationUseCase: SendUserNotificationUseCase
) {
    fun notifyStateChanged(state: ShooterGameState) {
        val notification = GameStateResponse
            .convert(state)
            .let(Json::encodeToString)
            .let(NotificationContent::Data)

        val sendToPlayer: (Long) -> Unit = { playerId -> sendState(playerId, notification) }
        state.playerIds.forEach(sendToPlayer)
    }

    fun notifyAction(action: ShooterGameActions, playerId: Long) = notifyAction(
        action = action,
        playerIds = listOf(playerId)
    )

    fun notifyAction(action: ShooterGameActions, playerIds: List<Long>) {
        val command = when (action) {
            is ShooterGameActions.Hit -> WSCommand.ActionHit
            is ShooterGameActions.Shoot -> WSCommand.ActionShoot
            ShooterGameActions.LifecycleCompleted -> return
        }

        val notification = action
            .let(Json::encodeToString)
            .let(NotificationContent::Data)
        playerIds.forEach { playerId ->
            sendUserNotificationUseCase.send(playerId, command.id, notification)
        }
    }

    private fun sendState(
        userId: Long,
        content: NotificationContent.Data
    ) = sendUserNotificationUseCase.send(userId, WSCommand.GameState.id, content)

    private fun GameStateResponse.Companion.convert(state: ShooterGameState) = GameStateResponse(
        typeCode = state.responseType.code,
        winnerTeamId = ShooterGameState.finished.winnerTeam.getOrNull(state)?.id ?: 0,
        playerData = state.playersData
    )

    @Serializable
    private data class GameStateResponse(
        val typeCode: Long,
        val playerData: List<PlayerDataResponse>,
        val winnerTeamId: Int
    )

    @Serializable
    private data class PlayerDataResponse(
        val playerId: Long,
        val teamId: Int,
        val kills: Int = 0,
        val death: Int = 0,
        val alive: Boolean,
        val hp: Int,
        val pos: Transform,
        val verticalLookAngle: Float,
        val selectedWeaponId: Long
    )

    private fun PlayerDataResponse.Companion.fromDataOnly(
        id: Long,
        data: ShooterPlayerData
    ) = PlayerDataResponse(
        playerId = id,
        teamId = data.team.id,
        kills = data.kills,
        death = data.death,
        alive = true,
        hp = 0,
        pos = Transform.Zero,
        verticalLookAngle = 0f,
        selectedWeaponId = 0L
    )

    private val ShooterGameState.playersData: List<PlayerDataResponse>
        get() = when (this) {
            is Preparing -> pendingPlayers.map { (id, data) ->
                PlayerDataResponse.fromDataOnly(id, data)
            }

            is PlayingState -> players.map { (id, state) ->
                val playingState = ShooterPlayerState.dynamicState.playing.getOrNull(state)
                val killedState = ShooterPlayerState.dynamicState.killed.getOrNull(state)
                PlayerDataResponse(
                    playerId = id,
                    teamId = state.data.team.id,
                    kills = state.data.kills,
                    death = state.data.death,
                    alive = state.dynamicState is Killed,
                    hp = playingState?.hp ?: 0,
                    pos = playingState?.transform
                        ?: killedState?.killPosition
                        ?: Transform.Zero,
                    verticalLookAngle = playingState?.verticalLookAngle ?: 0f,
                    selectedWeaponId = 0L
                )
            }

            is Finished -> finishedPlayers.map { (id, data) ->
                PlayerDataResponse.fromDataOnly(id, data)
            }
        }

    private val ShooterGameState.responseType: GameStateTypeResponse
        get() = when (this) {
            is Preparing -> GameStateTypeResponse.Preparing
            is PlayingState -> GameStateTypeResponse.Playing
            is Finished -> GameStateTypeResponse.Finished
        }

    enum class GameStateTypeResponse(val code: Long) {
        Preparing(1L),
        Playing(2L),
        Finished(3L),
    }
}