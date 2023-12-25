package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.UserNotFoundDefaultResponse
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.shooter.domain.repositories.IShooterGamePlayerStatsRepository
import com.denisrebrof.user.gateways.WSUserEmptyRequestHandler
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterPlayerStatsRequestListener @Autowired constructor(
    private val playerStatsRepository: IShooterGamePlayerStatsRepository,
) : WSUserEmptyRequestHandler(WSCommand.PlayerStats.id) {
    override fun handleMessage(userId: Long): ResponseState = playerStatsRepository
        .getPlayerStats(userId)
        ?.let(Json::encodeToString)
        ?.let(ResponseState::CreatedResponse)
        ?: UserNotFoundDefaultResponse
}