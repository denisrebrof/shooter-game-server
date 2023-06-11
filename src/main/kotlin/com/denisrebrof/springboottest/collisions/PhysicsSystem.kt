package com.denisrebrof.springboottest.collisions

import com.denisrebrof.springboottest.qtree.QTCollider
import com.denisrebrof.springboottest.qtree.QuadTree
import java.awt.Rectangle


class PhysicsSystem(
        bounds: Rectangle
) {
    private val tree = QuadTree<QTCollider>(0, bounds)

    private val staticColliders: MutableList<QTCollider> = mutableListOf()
    private val dynamicColliders: MutableList<QTCollider> = mutableListOf()

    fun update() {
        tree.clear()
        staticColliders.forEach(tree::insert)
        dynamicColliders.forEach(tree::insert)

        val possibleCollisions: MutableList<QTCollider> = ArrayList()
        for (current in dynamicColliders) {
            possibleCollisions.clear()
            tree.retrieve(possibleCollisions, current)
            for (collider in possibleCollisions) {

            }
        }
    }
}