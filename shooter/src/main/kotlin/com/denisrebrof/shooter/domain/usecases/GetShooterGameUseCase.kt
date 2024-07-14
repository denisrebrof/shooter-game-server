package com.denisrebrof.shooter.domain.usecases

import com.denisrebrof.matches.domain.services.MatchService
import com.denisrebrof.shooter.domain.services.ShooterGameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetShooterGameUseCase @Autowired constructor(
    private val service: ShooterGameService,
    private val matchService: MatchService
) {
    fun get(userId: Long) = matchService
        .getMatchIdByUserId(userId)
        ?.let(service::get)

    fun getAll() = matchService
        .getMatches()
        .mapNotNull { match -> service.get(match.id) }
}