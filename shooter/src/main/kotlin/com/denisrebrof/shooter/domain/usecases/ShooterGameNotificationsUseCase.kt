package com.denisrebrof.shooter.domain.usecases

import arrow.optics.dsl.index
import arrow.optics.typeclasses.Index
import com.denisrebrof.commands.domain.model.NotificationContent
import com.denisrebrof.commands.domain.model.NotificationContent.Companion.toNotificationData
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.*
import com.denisrebrof.user.domain.SendUserNotificationUseCase
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterGameNotificationsUseCase @Autowired constructor(
    private val sendUserNotificationUseCase: SendUserNotificationUseCase
) {
    fun notifyStateChanged(state: ShooterGameState) {
        val notification = GameStateResponse
            .convert(state)
            .toNotificationData()

        val sendToPlayer: (Long) -> Unit = { playerId -> sendState(playerId, notification) }
        state.playerIds.forEach(sendToPlayer)
    }

    fun notifyAction(action: ShooterGameActions, vararg playerIds: Long) {
        val command = when (action) {
            is ShooterGameActions.Hit -> WSCommand.ActionHit
            is ShooterGameActions.Shoot -> WSCommand.ActionShoot
            is ShooterGameActions.JoinedStateChange -> WSCommand.ActionJoinedStateChange
            ShooterGameActions.LifecycleCompleted -> return
        }
        val notification = when (action) {
            is ShooterGameActions.Hit -> action.toNotificationData()
            is ShooterGameActions.JoinedStateChange -> action.toNotificationData()
            is ShooterGameActions.Shoot -> action.toNotificationData()
            ShooterGameActions.LifecycleCompleted -> return
        }

        playerIds.forEach { playerId ->
            sendUserNotificationUseCase.send(playerId, command.id, notification)
        }
    }

    private fun sendState(
        userId: Long,
        content: NotificationContent.Data
    ) = sendUserNotificationUseCase.send(userId, WSCommand.GameState.id, content)

    private fun GameStateResponse.Companion.convert(state: ShooterGameState): GameStateResponse {
        val getTeamStats: (PlayerTeam) -> TeamStateResponse = kills@{
            val playingTeamData = ShooterGameState
                .playingState
                .teamData
                .index(Index.map(), it)
                .getOrNull(state)

            if (playingTeamData != null)
                return@kills playingTeamData.toResponse()

            val finishedTeamData = ShooterGameState
                .finished
                .teamData
                .index(Index.map(), it)
                .getOrNull(state)

            if (finishedTeamData != null)
                return@kills finishedTeamData.toResponse()

            return@kills TeamStateResponse(0, 0, Transform.Zero, false, 0L)
        }

        return GameStateResponse(
            typeCode = state.responseType.code,
            playersHash = state.participantIds.hashCode(),
            playerData = state.playersData,
            winnerTeamId = ShooterGameState.finished.winnerTeam.getOrNull(state)?.id ?: 0,
            redTeamState = getTeamStats(PlayerTeam.Red),
            blueTeamState = getTeamStats(PlayerTeam.Blue),
        )
    }

    @Serializable
    private data class GameStateResponse(
        val typeCode: Long,
        val playersHash: Int,
        val playerData: List<PlayerDataResponse>,
        val winnerTeamId: Int,
        val redTeamState: TeamStateResponse,
        val blueTeamState: TeamStateResponse,
    )

    @Serializable
    private data class TeamStateResponse(
        val teamFlags: Int,
        val teamKills: Int,
        val flagPos: Transform,
        val flagHasOwner: Boolean,
        val flagOwnerId: Long
    )

    private fun TeamPlayingData.toResponse() = TeamStateResponse(
        teamFlags = flagsTaken,
        teamKills = kills,
        flagPos = flagPos,
        flagHasOwner = flagPlayerId != null,
        flagOwnerId = flagPlayerId ?: 0L,
    )

    private fun FinishedTeamData.toResponse() = TeamStateResponse(
        teamFlags = flagsTaken,
        teamKills = kills,
        flagPos = Transform.Zero,
        flagHasOwner = false,
        flagOwnerId = 0L,
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
        val selectedWeaponId: Long,
        val crouching: Boolean,
        val jumping: Boolean,
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
        selectedWeaponId = 0L,
        crouching = false,
        jumping = false
    )

    private val ShooterGameState.playersData: List<PlayerDataResponse>
        get() = when (this) {
            is Preparing -> pendingPlayers.map { (id, data) ->
                PlayerDataResponse.fromDataOnly(id, data)
            }

            is PlayingState -> players.plus(botStates).map { (id, state) ->
                val playingState = ShooterPlayerState.dynamicState.playing.getOrNull(state)
                val killedState = ShooterPlayerState.dynamicState.killed.getOrNull(state)
                PlayerDataResponse(
                    playerId = id,
                    teamId = state.data.team.id,
                    kills = state.data.kills,
                    death = state.data.death,
                    alive = state.dynamicState is Playing,
                    hp = playingState?.hp ?: 0,
                    pos = playingState?.transform
                        ?: killedState?.killPosition
                        ?: Transform.Zero,
                    verticalLookAngle = playingState?.verticalLookAngle ?: 0f,
                    selectedWeaponId = state.selectedWeaponId,
                    crouching = playingState?.crouching ?: false,
                    jumping = playingState?.jumping ?: false
                )
            }

            is Finished -> finishedPlayers.plus(finishedBots).map { (id, data) ->
                PlayerDataResponse.fromDataOnly(id, data)
            }
        }
}