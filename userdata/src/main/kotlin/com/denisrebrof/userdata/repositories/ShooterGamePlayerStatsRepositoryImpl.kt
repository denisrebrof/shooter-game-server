package com.denisrebrof.userdata.repositories

import com.denisrebrof.shooter.domain.model.ShooterGamePlayerStats
import com.denisrebrof.shooter.domain.model.ShooterGameRating
import com.denisrebrof.shooter.domain.repositories.IShooterGamePlayerStatsRepository
import com.denisrebrof.userdata.internal.UserDataRepository
import com.denisrebrof.userdata.model.UserData
import com.denisrebrof.userdata.model.UserDataMapper.toUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ShooterGamePlayerStatsRepositoryImpl @Autowired constructor(
    private val userDataRepository: UserDataRepository,
) : IShooterGamePlayerStatsRepository {

    private val lessRatingPreferredSize = 2

    override fun handleMatchResults(userId: Long, won: Boolean, kills: Int, death: Int) {
        userDataRepository
            .findUserDataById(userId)
            ?.run {
                copy(
                    kills = this.kills + kills,
                    death = this.death + death,
                    gamesPlayed = this.gamesPlayed + 1,
                    gamesWon = if (won) gamesWon + 1 else gamesWon,
                    rating = kills
                        .minus(death * 0.33f)
                        .toInt()
                        .coerceAtLeast(0)
                        .plus(this.rating + 1),
                )
            }
            ?.let(userDataRepository::save)
    }

    override fun getRating(userId: Long, size: Int): List<ShooterGameRating> {
        if (size == 0)
            return emptyList()

        val user = userDataRepository
            .findUserDataById(userId)
            ?: return emptyList()

        val userPos = userDataRepository.getUserRatingPos(userId, user.rating)

        if (size == 1)
            return getRating(userPos, user).let(::listOf)

        val pageable = Pageable.ofSize(size)

        var lessRatingUsers = userDataRepository
            .getUsersWithRatingLess(userId, user.rating, pageable)

        var moreRatingUsers: List<UserData> = userDataRepository
            .getUsersWithRatingMore(userId, user.rating, pageable)

        val ratingLessFreeSpace = size
            .minus(moreRatingUsers.size)
            .minus(1)

        val lessRatingSize = maxOf(lessRatingPreferredSize, ratingLessFreeSpace)
            .coerceAtMost(lessRatingUsers.size)

        val moreRatingSize = size
            .minus(lessRatingSize)
            .minus(1)
            .coerceAtMost(moreRatingUsers.size)

        lessRatingUsers = lessRatingUsers.take(lessRatingSize)
        moreRatingUsers = moreRatingUsers.take(moreRatingSize)

        return lessRatingUsers
            .plus(user)
            .plus(moreRatingUsers)
            .mapIndexed { index, userData ->
                val pos = lessRatingSize - index + userPos + 1
                getRating(pos, userData)
            }
    }

    override fun getPlayerStats(userId: Long): ShooterGamePlayerStats? {
        val user = userDataRepository
            .findUserDataById(userId)
            ?: return null

        return ShooterGamePlayerStats(
            user.kills,
            user.death
        )
    }

    private fun getRating(pos: Long, user: UserData) = ShooterGameRating(
        user = user.toUser(),
        pos,
        user.rating
    )
}