package com.denisrebrof.springboottest.qtree

import java.awt.Rectangle
import java.awt.geom.Rectangle2D
import kotlin.math.abs
import kotlin.math.min

class QTCollider(
        val id: Long,
        private val shape: Shape,
        r: Rectangle
) : Rectangle(r) {

    private val circleRadius: Int
        get() = min(width, height)

    fun intersects(other: QTCollider): Boolean = when (shape) {
        Shape.Box -> when (other.shape) {
            Shape.Box -> intersects(other as Rectangle2D)
            Shape.Circle -> intersectsBoxWithCircle(other)
        }

        Shape.Circle -> when (other.shape) {
            Shape.Box -> other.intersectsBoxWithCircle(this)
            Shape.Circle -> intersectsCircleWithCircle(other)
        }
    }

    private fun intersectsCircleWithCircle(otherRect: QTCollider): Boolean {
        val distX = abs(otherRect.centerX - centerX)
        val distY = abs(otherRect.centerY - centerY)
        val sqDist = distX * distX + distY * distY
        val sumRad = circleRadius + otherRect.circleRadius
        return sqDist < sumRad * sumRad
    }

    private fun intersectsBoxWithCircle(circleRect: QTCollider): Boolean {
        val circleCenterX = circleRect.centerX
        val circleCenterY = circleRect.centerY
        val closestX = circleCenterX.coerceIn(minX, maxX)
        val closestY = circleCenterY.coerceIn(minY, maxY)
        val distanceX = circleCenterX - closestX
        val distanceY = circleCenterY - closestY
        val distanceSquared = distanceX * distanceX + distanceY * distanceY
        val circleRadius = circleRect.circleRadius
        return distanceSquared < circleRadius * circleRadius
    }

    enum class Shape {
        Box,
        Circle
    }
}