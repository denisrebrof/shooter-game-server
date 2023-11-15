package com.denisrebrof.games

import kotlinx.serialization.Serializable

@Serializable
data class Transform(
    val x: Float,
    val y: Float,
    val z: Float,
    val r: Float,
) {
    fun isClose(other: Transform, distance: Float): Boolean {
        val squaredDist = other.getSquaredDistanceTo(this)
        return squaredDist <= distance * distance
    }

    private fun getSquaredDistanceTo(other: Transform): Float {
        val distX = other.x - x
        val distY = other.y - y
        val distZ = other.z - z
        return distX * distX + distY * distY + distZ * distZ
    }

    companion object {
        val Zero = Transform(0f,0f,0f,0f)
    }
}