package com.denisrebrof.shooter.domain.game

import arrow.atomic.AtomicLong
import arrow.atomic.value
import com.denisrebrof.games.Transform
import com.denisrebrof.shooter.domain.model.*

class PlayerStateFactory(
    private val settings: ShooterGameSettings
) {
    private val botWId: AtomicLong = AtomicLong(1L)

    fun createJoinedPlayerState(
        team: PlayerTeam,
        pos: Transform
    ): ShooterPlayerState = ShooterPlayerState(
        data = ShooterPlayerData(team),
        dynamicState = createPlayerPlayingState(pos),
        selectedWeaponId = 0L,
    )

    fun createJoinedBotState(
        team: PlayerTeam,
        pos: Transform
    ): ShooterBotState {
        val weaponId = botWId.value
        botWId.value = (weaponId + 1) % 8
        return ShooterBotState(
            playerState = ShooterPlayerState(
                data = ShooterPlayerData(team),
                selectedWeaponId = weaponId,
                dynamicState = createPlayerPlayingState(pos)
            ),
            routeIndex = settings.botSettings.findCloseRouteIndex(pos, team),
            routePointIndex = 0
        )
    }

    private fun createPlayerPlayingState(pos: Transform) = Playing(
        hp = settings.defaultHp,
        transform = pos,
        verticalLookAngle = 0f,
        crouching = false,
        aiming = false
    )
}

