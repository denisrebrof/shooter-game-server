package com.denisrebrof.springboottest.qtree

import java.awt.Rectangle
import java.awt.geom.Rectangle2D


class QuadTree<RType : Rectangle2D>(
        private val level: Int,
        private val bounds: Rectangle2D,
        private val nodeCapacity: Int = 10,
        private val maxLevels: Int = 5
) {

    private val objects: MutableList<RType> = mutableListOf()
    private val nodes = arrayOfNulls<QuadTree<RType>?>(4)

    fun clear() {
        objects.clear()
        for (i in nodes.indices) {
            if (nodes[i] != null) {
                nodes[i]?.clear()
                nodes[i] = null
            }
        }
    }

    private fun split() {
        val subWidth = (bounds.width / 2).toInt()
        val subHeight = (bounds.height / 2).toInt()
        val x = bounds.x.toInt()
        val y = bounds.y.toInt()
        nodes[0] = QuadTree(level + 1, Rectangle(x + subWidth, y, subWidth, subHeight))
        nodes[1] = QuadTree(level + 1, Rectangle(x, y, subWidth, subHeight))
        nodes[2] = QuadTree(level + 1, Rectangle(x, y + subHeight, subWidth, subHeight))
        nodes[3] = QuadTree(level + 1, Rectangle(x + subWidth, y + subHeight, subWidth, subHeight))
    }

    /*
     * Determine which node the object belongs to. -1 means
     * object cannot completely fit within a child node and is part
     * of the parent node
     */
    private fun getIndex(pRect: RType): Int {
        var index = -1
        val verticalMidpoint = bounds.x + bounds.width / 2
        val horizontalMidpoint = bounds.y + bounds.height / 2
        // Object can completely fit within the top quadrants
        val topQuadrant = pRect.y < horizontalMidpoint && pRect.y + pRect.height < horizontalMidpoint
        // Object can completely fit within the bottom quadrants
        val bottomQuadrant = pRect.y > horizontalMidpoint
        // Object can completely fit within the left quadrants
        if (pRect.x < verticalMidpoint && pRect.x + pRect.width < verticalMidpoint) {
            if (topQuadrant) {
                index = 1
            } else if (bottomQuadrant) {
                index = 2
            }
        } else if (pRect.x > verticalMidpoint) {
            if (topQuadrant) {
                index = 0
            } else if (bottomQuadrant) {
                index = 3
            }
        }
        return index
    }

    /*
     * Insert the object into the QuadTree. If the node
     * exceeds the capacity, it will split and add all
     * objects to their corresponding nodes.
     */
    fun insert(pRect: RType) {
        if (nodes[0] != null) {
            val index = getIndex(pRect)
            if (index != -1) {
                nodes[index]?.insert(pRect)
                return
            }
        }
        objects.add(pRect)
        if (objects.size > nodeCapacity && level < maxLevels) {
            if (nodes[0] == null) {
                split()
            }
            var i = 0
            while (i < objects.size) {
                val index = getIndex(objects[i])
                if (index != -1) {
                    nodes[index]?.insert(objects.removeAt(i))
                } else {
                    i++
                }
            }
        }
    }

    /*
     * Return all objects that could collide with the given object
     */
    fun retrieve(returnObjects: MutableList<RType>, pRect: RType): List<*> {
        val index = getIndex(pRect)
        if (index != -1 && nodes[0] != null) {
            nodes[index]?.retrieve(returnObjects, pRect)
        }
        returnObjects.addAll(objects)
        return returnObjects
    }
}

