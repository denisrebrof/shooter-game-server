package com.denisrebrof.simplestats.presenataion.controller

import com.denisrebrof.simplestats.domain.SimpleStatsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SimpleStatsController @Autowired constructor(
    private val statsService: SimpleStatsService
) {
    private val logsList
        get() = statsService
            .getLogs()
            .joinToString(separator = "<br>")

    private val propsList
        get() = statsService
            .getProperties()
            .toList()
            .joinToString(separator = "<br>") { (k, v) -> "$k : $v" }

    @GetMapping("/stats")
    fun getStats(): String = buildString {
        appendLine("Properties<br><br>")
        appendLine(propsList)
        appendLine("<br>")
        appendLine("Logs list<br>")
        appendLine("Current time: ${statsService.currentDateText}<br><br>")
        appendLine(logsList)
    }
}