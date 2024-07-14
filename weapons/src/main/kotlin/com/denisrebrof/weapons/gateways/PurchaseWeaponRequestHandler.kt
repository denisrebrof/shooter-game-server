package com.denisrebrof.weapons.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.commands.domain.model.fromBoolean
import com.denisrebrof.user.gateways.WSUserRequestHandler
import com.denisrebrof.weapons.domain.PurchaseWeaponUseCase
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PurchaseWeaponRequestHandler @Autowired constructor(
    private val purchaseWeaponUseCase: PurchaseWeaponUseCase,
    private val weaponStatesResponseDelegate: WeaponStatesResponseDelegate
) : WSUserRequestHandler<PurchaseWeaponRequestHandler.PurchaseRequest>(WSCommand.PurchaseWeapon.id) {

    override fun parseData(data: String): PurchaseRequest = Json.decodeFromString(data)

    override fun handleMessage(userId: Long, data: PurchaseRequest): ResponseState {
        val purchaseResult = when {
            data.isUpgrade -> purchaseWeaponUseCase.upgrade(userId, data.weaponId)
            else -> purchaseWeaponUseCase.purchase(userId, data.weaponId)
        }

        if (purchaseResult)
            weaponStatesResponseDelegate.notifyWeaponsStates(userId)

        return ResponseState.fromBoolean(purchaseResult)
    }

    @Serializable
    data class PurchaseRequest(
        val weaponId: Long,
        val isUpgrade: Boolean,
    )
}