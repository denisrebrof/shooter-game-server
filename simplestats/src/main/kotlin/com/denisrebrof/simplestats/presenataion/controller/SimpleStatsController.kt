package com.denisrebrof.simplestats.presenataion.controller

import com.denisrebrof.simplestats.domain.SimpleStatsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SimpleStatsController @Autowired constructor(
    private val statsService: SimpleStatsService
) {
    @GetMapping("/my_logs")
    fun getLogs(): String = statsService
        .getLogs()
        .joinToString(separator = "<br>")

    @GetMapping("/stat_props")
    fun getProps(): String = statsService
        .getProperties()
        .toList()
        .joinToString(separator = "<br>") { (k, v) -> "$k : $v" }
}