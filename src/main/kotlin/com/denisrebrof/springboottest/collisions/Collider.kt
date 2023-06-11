package com.denisrebrof.springboottest.collisions

import kotlin.math.sqrt

sealed class Collider(
        open var x: Double,
        open var y: Double,
) {
    protected fun getSquaredDistanceTo(x: Double, y: Double): Double {
        val distX = x - this.x
        val distY = y - this.y
        return distX * distX + distY * distY
    }

    abstract fun contains(x: Double, y: Double): Boolean

    abstract val rectangle:

    fun collides(other: Collider): Boolean = when (this) {
        is Box -> when (other) {
            is Box -> hasCollision(other)
            is Circle -> hasCollision(other)
        }

        is Circle -> when (other) {
            is Box -> other.hasCollision(this)
            is Circle -> hasCollision(other)
        }
    }

    private fun Box.hasCollision(other: Box): Boolean = arrayOf(
            right >= other.left,
            left <= other.right,
            top >= other.bottom,
            bottom <= other.top
    ).all { it }

    private fun Box.hasCollision(other: Circle): Boolean {
        val closestX = other.x.coerceIn(left, right)
        val closestY = other.y.coerceIn(bottom, top)
        val distanceX = other.x - closestX
        val distanceY = other.y - closestY
        val distanceSquared = distanceX * distanceX + distanceY * distanceY
        return distanceSquared < other.radius * other.radius
    }

    private fun Circle.hasCollision(other: Circle): Boolean {
        val collisionDistance = radius + other.radius
        val squaredDist = getSquaredDistanceTo(other.x, other.y)
        return squaredDist < collisionDistance * collisionDistance
    }

    class Box(
            override var x: Double,
            override var y: Double,
            width: Double,
            height: Double
    ) : Collider(x, y) {

        private val halfWidth = width / 2
        private val halfHeight = height / 2

        val left: Double
            get() = x - halfWidth

        val right: Double
            get() = x + halfWidth

        val bottom: Double
            get() = y - halfHeight

        val top: Double
            get() = y + halfHeight

        override fun contains(x: Double, y: Double): Boolean {
            val xRange = x - halfWidth..x + halfWidth
            val yRange = y - halfHeight..y + halfHeight
            return x in xRange && y in yRange
        }
    }

    class Circle(
            override var x: Double,
            override var y: Double,
            val radius: Double
    ) : Collider(x, y) {
        private val squaredRadius = radius * radius

        override fun contains(x: Double, y: Double): Boolean = getSquaredDistanceTo(x, y) < squaredRadius
    }
}