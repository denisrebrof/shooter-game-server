package com.denisrebrof.springboottest.shooter.presentation

import com.denisrebrof.springboottest.commands.domain.model.ResponseState
import com.denisrebrof.springboottest.commands.domain.model.WSCommand
import com.denisrebrof.springboottest.matches.domain.IMatchRepository
import com.denisrebrof.springboottest.shooter.ShooterGameService
import com.denisrebrof.springboottest.user.gateways.WSUserRequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.ShooterGameIntents
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterSelectWeaponRequestHandler @Autowired constructor(
    private val service: ShooterGameService,
    private val matchRepository: IMatchRepository
) : WSUserRequestHandler<ShooterSelectWeaponRequestHandler.SelectWeaponRequest>(WSCommand.IntentSelectWeapon.id) {

    override fun parseData(data: String): SelectWeaponRequest = Json.decodeFromString(data)

    override fun handleMessage(userId: Long, data: SelectWeaponRequest): ResponseState = with(data) {
        val matchId = matchRepository.getMatchIdByUserId(userId) ?: return@with ResponseState.NoResponse
        val intent = ShooterGameIntents.SelectWeapon(userId, data.weaponId)
        service.submitIntent(matchId, intent)
        return@with ResponseState.NoResponse
    }

    @Serializable
    data class SelectWeaponRequest(
        val weaponId: Long,
    )
}