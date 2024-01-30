package com.denisrebrof.progression.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LevelData(
    val xpToReach: Int = 0
)
