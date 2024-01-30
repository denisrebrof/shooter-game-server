package com.denisrebrof.progression.domain.repositories

import com.denisrebrof.progression.domain.model.LevelData

interface ILevelDataRepository {
    fun getLevelData(levelId: Int): LevelData
}