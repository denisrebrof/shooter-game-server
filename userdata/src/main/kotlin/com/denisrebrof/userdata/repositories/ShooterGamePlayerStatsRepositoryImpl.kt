package com.denisrebrof.userdata.repositories

import com.denisrebrof.shooter.domain.repositories.IShooterGamePlayerStatsRepository
import com.denisrebrof.userdata.internal.UserDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShooterGamePlayerStatsRepositoryImpl @Autowired constructor(
    private val userDataRepository: UserDataRepository,
) : IShooterGamePlayerStatsRepository {
    override fun handleMatchResults(userId: Long, won: Boolean, kills: Int, death: Int) {
        userDataRepository
            .findUserDataById(userId)
            ?.run {
                copy(
                    kills = this.kills + kills,
                    death = this.death + death,
                    gamesPlayed = this.gamesPlayed + 1,
                    gamesWon = if (won) gamesWon + 1 else gamesWon
                )
            }
            ?.let(userDataRepository::save)
    }
}