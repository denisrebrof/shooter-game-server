package com.denisrebrof.games

import kotlinx.serialization.Serializable
import kotlin.math.atan2
import kotlin.math.sqrt

private val invertedPi = 1f
    .div(Math.PI)
    .toFloat()

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

    fun lookAt(target: Transform): Transform {
        val relativeX = target.x.minus(x)
        val relativeZ = target.z.minus(z)
        val rot = atan2(relativeX, relativeZ) * invertedPi * 180f
        return copy(r = rot)
    }

    companion object {
        val Zero = Transform(0f, 0f, 0f, 0f)
    }
}