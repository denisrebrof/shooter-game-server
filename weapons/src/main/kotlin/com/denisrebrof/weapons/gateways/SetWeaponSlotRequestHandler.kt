package com.denisrebrof.weapons.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.fromBoolean
import com.denisrebrof.user.gateways.WSUserRequestHandler
import com.denisrebrof.weapons.domain.SetWeaponSlotUseCase
import com.denisrebrof.weapons.domain.model.WeaponSlot
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SetWeaponSlotRequestHandler @Autowired constructor(
    private val setWeaponSlotUseCase: SetWeaponSlotUseCase,
    private val weaponStatesResponseDelegate: WeaponStatesResponseDelegate
) : WSUserRequestHandler<SetWeaponSlotRequestHandler.SetWeaponSlotRequest>(WSCommand.SetWeaponSlot.id) {

    override fun parseData(data: String) = Json.decodeFromString<SetWeaponSlotRequest>(data)

    override fun handleMessage(userId: Long, data: SetWeaponSlotRequest): ResponseState {
        val setSlotResult = setWeaponSlotUseCase.setWeaponSlot(
            userId = userId,
            weaponId = data.weaponId,
            slot = getSlot(data.primary)
        )

        if (setSlotResult)
            weaponStatesResponseDelegate.notifyWeaponsStates(userId)

        return ResponseState.fromBoolean(setSlotResult)
    }

    private fun getSlot(isPrimary: Boolean) = when {
        isPrimary -> WeaponSlot.Primary
        else -> WeaponSlot.Secondary
    }

    @Serializable
    data class SetWeaponSlotRequest(
        val weaponId: Long,
        val primary: Boolean,
    )
}