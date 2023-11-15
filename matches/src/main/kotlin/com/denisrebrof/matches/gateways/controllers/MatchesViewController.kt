package com.denisrebrof.matches.gateways.controllers

import com.denisrebrof.matches.domain.services.MatchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MatchesViewController @Autowired constructor(
    private val matchService: MatchService
) {
    @GetMapping("/matches")
    fun getMatches(): String = matchService.getMatches().toString()
}