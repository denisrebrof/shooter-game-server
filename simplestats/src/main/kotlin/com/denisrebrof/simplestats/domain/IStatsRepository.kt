package com.denisrebrof.simplestats.domain

interface IStatsRepository {
    fun sendServerEvent(eventName: String, vararg eventParams: String)
    fun sendUserEvent(userId: Long, eventName: String, vararg eventParams: String)
}