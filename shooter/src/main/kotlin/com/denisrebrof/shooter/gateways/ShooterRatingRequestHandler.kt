package com.denisrebrof.shooter.gateways

import com.denisrebrof.commands.domain.model.ResponseState
import com.denisrebrof.commands.domain.model.WSCommand
import com.denisrebrof.shooter.domain.model.ShooterGameRating
import com.denisrebrof.shooter.domain.repositories.IShooterGamePlayerStatsRepository
import com.denisrebrof.user.gateways.WSUserRequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterRatingRequestHandler @Autowired constructor(
    private val playerStatsRepository: IShooterGamePlayerStatsRepository,
) : WSUserRequestHandler<Int>(WSCommand.Rating.id) {

    override fun parseData(data: String): Int = data.toInt()

    override fun handleMessage(userId: Long, data: Int): ResponseState = playerStatsRepository
        .getRating(userId, data)
        .map { rating -> getResponseItem(rating, userId) }
        .let(::RatingDataResponse)
        .let(Json.Default::encodeToString)
        .let(ResponseState::CreatedResponse)

    private fun getResponseItem(
        rating: ShooterGameRating,
        userId: Long
    ) = RatingDataResponseItem(
        position = rating.position,
        rating = rating.rating,
        username = rating.user.username,
        isMine = rating.user.id == userId
    )

    @Serializable
    private data class RatingDataResponse(
        val items: List<RatingDataResponseItem>
    )

    @Serializable
    private data class RatingDataResponseItem(
        val position: Long,
        val rating: Int,
        val username: String,
        val isMine: Boolean,
    )
}