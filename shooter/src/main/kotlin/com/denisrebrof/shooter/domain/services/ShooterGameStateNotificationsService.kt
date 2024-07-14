package com.denisrebrof.shooter.domain.services

import com.denisrebrof.commands.domain.model.NotificationContent
import com.denisrebrof.commands.domain.model.ResponseErrorCodes
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.matches.domain.model.Match
import com.denisrebrof.matches.domain.services.IMatchServiceListener
import com.denisrebrof.shooter.domain.model.ShooterPlayerGameState
import com.denisrebrof.user.domain.SendUserNotificationUseCase
import com.denisrebrof.weapons.domain.GetPlayerWeaponUseCase
import com.denisrebrof.weapons.domain.model.WeaponSlot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterGameStateNotificationsService @Autowired constructor(
    private val sendUserNotificationUseCase: SendUserNotificationUseCase,
    private val getPlayerWeaponUseCase: GetPlayerWeaponUseCase
) : IMatchServiceListener {

    private val Match.participantsParams
        get() = participants.toLongArray()

    private val weaponNotFoundError = NotificationContent.Error(
        ResponseErrorCodes.Internal.code,
        Exception("Weapon Not Found")
    )

    private val inactiveResponse = ShooterPlayerGameState.Inactive
        .let(Json::encodeToString)
        .let(NotificationContent::Data)

    override fun onMatchStarted(match: Match) = notifyStateChanged(true,match.mapId, *match.participantsParams)
    override fun onMatchFinished(match: Match) = notifyStateChanged(false,match.mapId, *match.participantsParams,)
    override fun onJoined(match: Match, vararg participantIds: Long) = notifyStateChanged(true,match.mapId, *participantIds,)
    override fun onLeft(match: Match, vararg participantIds: Long) = notifyStateChanged(false,match.mapId, *participantIds,)

    private fun notifyStateChanged(
        state: Boolean,
        mapId: Int,
        vararg receiverIds: Long
    ) = receiverIds.forEach { userId ->
        val notification = getNotification(state, userId, mapId)
        sendUserNotificationUseCase.send(userId, WSCommand.GetMatch.id, notification)
    }

    private fun getNotification(
        state: Boolean,
        userId: Long,
        mapId: Int
    ): NotificationContent {
        if (!state)
            return inactiveResponse

        val playerGameState = ShooterPlayerGameState(
            gameActive = true,
            mapId = mapId,
            primaryWeapon = getPlayerWeaponUseCase
                .getWeapon(userId, WeaponSlot.Primary)
                ?: return weaponNotFoundError,
            secondaryWeapon = getPlayerWeaponUseCase
                .getWeapon(userId, WeaponSlot.Secondary)
                ?: return weaponNotFoundError,
        )

        return playerGameState
            .let(Json::encodeToString)
            .let(NotificationContent::Data)
    }
}