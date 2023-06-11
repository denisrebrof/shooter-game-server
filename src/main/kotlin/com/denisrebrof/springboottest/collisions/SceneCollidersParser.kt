package com.denisrebrof.springboottest.collisions

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object SceneCollidersParser {

    fun createFromJson(data: String): List<Collider> {
        val parsedContent = runCatching {
            Json.decodeFromString<List<ColliderData>>(data)
        }.onFailure {
            //Log error
        }.getOrDefault(listOf())

        return parsedContent.map(ColliderData::toCollider)
    }

    private data class ColliderData(
            val x: Double,
            val y: Double,
            val radius: Double,
            val width: Double,
            val height: Double
    ) {
        fun toCollider(): Collider = when (radius) {
            0.0 -> Collider.Box(x, y, width, height)
            else -> Collider.Circle(x, y, radius)
        }
    }
}