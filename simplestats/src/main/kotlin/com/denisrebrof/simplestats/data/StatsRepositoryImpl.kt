package com.denisrebrof.simplestats.data

import com.denisrebrof.simplestats.domain.IStatsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class StatsRepositoryImpl @Autowired constructor(
    private val userEventsRepository: IUserEventsRepository,
    private val serverEventsRepository: IServerEventsRepository,
) : IStatsRepository {
    override fun sendServerEvent(eventName: String, vararg eventParams: String) {
        ServerStatEventData(
            eventName = eventName,
            eventParam1 = eventParams.getOrNull(0) ?: "",
            eventParam2 = eventParams.getOrNull(1) ?: "",
            eventParam3 = eventParams.getOrNull(2) ?: "",
            eventParam4 = eventParams.getOrNull(3) ?: "",
            eventParam5 = eventParams.getOrNull(4) ?: "",
        ).let(serverEventsRepository::save)
    }

    override fun sendUserEvent(userId: Long, eventName: String, vararg eventParams: String) {
        UserStatEventData(
            eventName = eventName,
            userId = userId,
            eventParam1 = eventParams.getOrNull(0) ?: "",
            eventParam2 = eventParams.getOrNull(1) ?: "",
            eventParam3 = eventParams.getOrNull(2) ?: "",
            eventParam4 = eventParams.getOrNull(3) ?: "",
            eventParam5 = eventParams.getOrNull(4) ?: "",
        ).let(userEventsRepository::save)
    }

}

interface IUserEventsRepository : JpaRepository<UserStatEventData, Long>
interface IServerEventsRepository : JpaRepository<ServerStatEventData, Long>