package com.denisrebrof.weapons.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.toResponse
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import com.denisrebrof.weapons.domain.GetPlayerWeaponUseCase
import com.denisrebrof.weapons.domain.model.PlayerWeapon
import com.denisrebrof.weapons.domain.model.WeaponSlot
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LoadoutStateRequestHandler @Autowired constructor(
    private val getPlayerWeaponUseCase: GetPlayerWeaponUseCase
) : WSUserEmptyRequestHandler(WSCommand.LoadoutState.id) {
    override fun handleMessage(userId: Long): ResponseState {
        return LoadoutStateResponseData(
            primary = getPlayerWeaponUseCase
                .getWeapon(userId, WeaponSlot.Primary)
                ?: return ResponseState.NoResponse,
            secondary = getPlayerWeaponUseCase
                .getWeapon(userId, WeaponSlot.Secondary)
                ?: return ResponseState.NoResponse
        ).toResponse()
    }

    @Serializable
    private data class LoadoutStateResponseData(
        val primary: PlayerWeapon,
        val secondary: PlayerWeapon,
    )
}