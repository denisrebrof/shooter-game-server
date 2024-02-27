package com.denisrebrof.progression.domain.repositories

interface IUserProgressionRepository {
    fun getLevel(userId: Long): Int?
    fun setLevel(userId: Long, level: Int)
    fun getXp(userId: Long): Int?
    fun setXp(userId: Long, xp: Int)
    fun getLastClaimedLevel(userId: Long): Int?
    fun setLastClaimedLevel(userId: Long, level: Int)
}