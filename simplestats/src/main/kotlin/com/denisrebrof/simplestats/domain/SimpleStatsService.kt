package com.denisrebrof.simplestats.domain

import org.springframework.stereotype.Service
import java.util.*

@Service
class SimpleStatsService : ISimpleStatsReceiver {

    private val properties = Collections.synchronizedMap(mutableMapOf<String, ISimpleStatsProperty>())

    private val logs = Collections.synchronizedList(mutableListOf<String>())

    private val maxLogSize = 200

    val currentDateText: String
        get() = Date().toGMTString()

    fun getLogs(): List<String> = logs

    fun getProperties(): Map<String, String> = properties
        .mapValues { (_, value) -> value.getContent() }

    override fun addLog(content: String) {
        if (logs.size >= maxLogSize)
            logs.removeFirst()

        logs.add("[$currentDateText] $content")
    }

    override fun setProperty(name: String, property: ISimpleStatsProperty) {
        properties[name] = property
    }

    override fun setProperty(name: String, propertyProvider: () -> ISimpleStatsProperty) {
        properties[name] = propertyProvider.invoke()
    }
}