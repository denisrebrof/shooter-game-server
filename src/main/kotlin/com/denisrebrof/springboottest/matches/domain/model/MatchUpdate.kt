package com.denisrebrof.springboottest.matches.domain.model

data class MatchUpdate(
    val match: Match,
    val type: UpdateType
) {
    enum class UpdateType {
        Created,
        Removed
    }
}