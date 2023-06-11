package com.denisrebrof.springboottest.game

import com.denisrebrof.springboottest.collisions.PhysicsSystem
import io.reactivex.rxjava3.disposables.CompositeDisposable

class Game(
        private val physicsSystem: PhysicsSystem
) {

    private val subscriptions = CompositeDisposable()



    fun dispose() = subscriptions.clear()
}