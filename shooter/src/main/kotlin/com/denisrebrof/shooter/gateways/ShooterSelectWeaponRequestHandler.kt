package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.shooter.domain.model.ShooterGameIntents
import com.denisrebrof.shooter.domain.usecases.GetShooterGameUseCase
import com.denisrebrof.user.gateways.WSUserRequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterSelectWeaponRequestHandler @Autowired constructor(
    private val getGameUseCase: GetShooterGameUseCase
) : WSUserRequestHandler<ShooterSelectWeaponRequestHandler.SelectWeaponRequest>(WSCommand.IntentSelectWeapon.id) {

    override fun parseData(data: String): SelectWeaponRequest = Json.decodeFromString(data)

    override fun handleMessage(userId: Long, data: SelectWeaponRequest): ResponseState = with(data) {
        val game = getGameUseCase.get(userId) ?: return@with ResponseState.NoResponse
        ShooterGameIntents.SelectWeapon(userId, data.weaponId).let(game::submit)
        return@with ResponseState.NoResponse
    }

    @Serializable
    data class SelectWeaponRequest(val weaponId: Long)
}