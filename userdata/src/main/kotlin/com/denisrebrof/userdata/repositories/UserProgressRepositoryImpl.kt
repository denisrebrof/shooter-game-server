package com.denisrebrof.userdata.repositories

import com.denisrebrof.progression.domain.repositories.IUserProgressionRepository
import com.denisrebrof.userdata.internal.UserDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserProgressionRepositoryImpl @Autowired constructor(
    private val userDataRepository: UserDataRepository,
) : IUserProgressionRepository {
    override fun getLevel(userId: Long): Int? = userDataRepository
        .findUserDataById(userId)
        ?.level

    override fun setLevel(userId: Long, level: Int) {
        val data = userDataRepository.findUserDataById(userId) ?: return
        data.copy(level = level).let(userDataRepository::save)
    }

    override fun getXp(userId: Long): Int? = userDataRepository
        .findUserDataById(userId)
        ?.xp

    override fun setXp(userId: Long, xp: Int) {
        val data = userDataRepository.findUserDataById(userId) ?: return
        data.copy(xp = xp).let(userDataRepository::save)
    }
}