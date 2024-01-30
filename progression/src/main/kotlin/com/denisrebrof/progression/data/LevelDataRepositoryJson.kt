package com.denisrebrof.progression.data

import com.denisrebrof.progression.domain.model.LevelData
import com.denisrebrof.progression.domain.repositories.ILevelDataRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class LevelDataRepositoryJson @Autowired constructor(
    @Value("classpath:level-data.json")
    private val levelDataJson: Resource
) : ILevelDataRepository {

    @OptIn(ExperimentalSerializationApi::class)
    private val levelsData: Map<Int, LevelData> by lazy {
        val ld = Json
            .decodeFromStream<List<LevelData>>(levelDataJson.inputStream)
            .mapIndexed { index, data -> index + 1 to data }
            .toMap()
        ld
    }

    init {
        val a = levelsData.toString()
        println(a)
    }

    private val defaultLevelData = LevelData(1000)

    override fun getLevelData(levelId: Int) = levelsData[levelId] ?: defaultLevelData
}