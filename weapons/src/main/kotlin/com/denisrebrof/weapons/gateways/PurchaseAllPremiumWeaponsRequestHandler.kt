package com.denisrebrof.weapons.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.True
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import com.denisrebrof.weapons.domain.PurchaseAllPremiumWeaponsUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PurchaseAllPremiumWeaponsRequestHandler @Autowired constructor(
    private val purchaseWeaponsUseCase: PurchaseAllPremiumWeaponsUseCase,
) : WSUserEmptyRequestHandler(WSCommand.PurchaseAllWeapons.id) {

    override fun handleMessage(userId: Long): ResponseState {
        purchaseWeaponsUseCase.purchase(userId)
        return ResponseState.True
    }
}