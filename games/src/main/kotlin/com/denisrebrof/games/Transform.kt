package com.denisrebrof.games

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

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

    fun getSquaredDistanceTo(other: Transform): Float {
        val distX = other.x - x
        val distY = other.y - y
        val distZ = other.z - z
        return distX * distX + distY * distY + distZ * distZ
    }

    fun translateTo(target: Transform, amount: Float): Transform {
        val squaredDist = target.getSquaredDistanceTo(this)
        if (squaredDist < 0.001f)
            return target

        val changeMul = target
            .getSquaredDistanceTo(this)
            .let(::sqrt)
            .let(amount::div)

        return Transform(
            x = target.x.minus(x).times(changeMul).plus(x),
            y = target.y.minus(y).times(changeMul).plus(y),
            z = target.z.minus(z).times(changeMul).plus(z),
            r = r
        )
    }

    companion object {
        val Zero = Transform(0f, 0f, 0f, 0f)
    }
}