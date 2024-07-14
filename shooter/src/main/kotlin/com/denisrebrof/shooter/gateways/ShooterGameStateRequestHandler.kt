package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.*
import com.denisrebrof.matches.domain.services.MatchService
import com.denisrebrof.shooter.domain.model.ShooterPlayerGameState
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import com.denisrebrof.weapons.domain.GetPlayerWeaponUseCase
import com.denisrebrof.weapons.domain.model.WeaponSlot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterGameStateRequestHandler @Autowired constructor(
    private val matchService: MatchService,
    private val getPlayerWeaponUseCase: GetPlayerWeaponUseCase
) : WSUserEmptyRequestHandler(WSCommand.GetMatch.id) {

    private val weaponNotFoundError = ResponseState.ErrorResponse(
        ResponseErrorCodes.Internal.code,
        Exception("Weapon Not Found")
    )

    override fun handleMessage(userId: Long): ResponseState {
        val match = matchService
            .getMatchByUserId(userId)
            ?: return ShooterPlayerGameState.Inactive.toResponse()

        return ShooterPlayerGameState(
            gameActive = true,
            mapId = match.mapId,
            primaryWeapon = getPlayerWeaponUseCase
                .getWeapon(userId, WeaponSlot.Primary)
                ?: return weaponNotFoundError,
            secondaryWeapon = getPlayerWeaponUseCase
                .getWeapon(userId, WeaponSlot.Secondary)
                ?: return weaponNotFoundError,
        ).toResponse()
    }
}