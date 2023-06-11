package com.denisrebrof.springboottest.collisions

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader

@Service
class PhysicsSystemFactoryService @Autowired constructor(
        private val resourceLoader: ResourceLoader
) {

    private fun asString(resource: Resource): String = runCatching {
        InputStreamReader(resource.inputStream).use(FileCopyUtils::copyToString)
    }.onFailure {
        //Log error
    }.getOrDefault("")

    fun create(mapId: Long): PhysicsSystem = resourceLoader
            .getResource("classpath:maps/map$mapId.json")
            .let(::asString)
            .let(SceneCollidersParser::createFromJson)
            .let(::PhysicsSystem)
}