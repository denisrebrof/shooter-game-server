package com.denisrebrof.weapons.gateways

import com.denisrebrof.balance.domain.IncreaseBalanceUseCase
import com.denisrebrof.balance.domain.model.CurrencyType
import com.denisrebrof.commands.domain.model.*
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WeaponStatesRequestHandler @Autowired constructor(
    private val weaponStatesResponseDelegate: WeaponStatesResponseDelegate,
    private val increaseBalanceUseCase: IncreaseBalanceUseCase //TODO: Remove
) : WSUserEmptyRequestHandler(WSCommand.WeaponStates.id) {

    override fun handleMessage(userId: Long): ResponseState {
        increaseBalanceUseCase.increase(userId, 200, CurrencyType.Primary.id)

        return weaponStatesResponseDelegate
            .getWeaponStates(userId)
            ?.toResponse()
            ?: InternalErrorDefaultResponse
    }
}