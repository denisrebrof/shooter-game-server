package com.denisrebrof.simplestats.data

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "server_events")
data class ServerStatEventData(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val eventName: String = "",
    val eventTime: Date? = Date(),
    val eventParam1: String = "",
    val eventParam2: String = "",
    val eventParam3: String = "",
    val eventParam4: String = "",
    val eventParam5: String = "",
)

@Entity
@Table(name = "user_events")
data class UserStatEventData(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val userId: Long = 0L,
    val eventName: String = "",
    val eventTime: Date? = Date(),
    val eventParam1: String = "",
    val eventParam2: String = "",
    val eventParam3: String = "",
    val eventParam4: String = "",
    val eventParam5: String = "",
)
