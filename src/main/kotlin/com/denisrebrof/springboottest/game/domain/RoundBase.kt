package com.denisrebrof.springboottest.game.domain

import com.denisrebrof.springboottest.utils.subscribeOnIO
import com.denisrebrof.springboottest.utils.subscribeWithLogError
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

abstract class RoundBase(
    private val updateIntervalMs: Long = 100L,
    private val durationMs: Long = 60000L
) {
    protected val gameUpdateDisposables = CompositeDisposable()

    private var startTime: Long = 0L

    protected var isFinished: Boolean = false
        private set

    private fun internalUpdate() {
        val timeLeftMs = startTime + durationMs - System.currentTimeMillis()
        if (timeLeftMs < 0L)
            return onTimeLeft()

        update(timeLeftMs)
    }

    fun start(): Boolean {
        startTime = System.currentTimeMillis()
        return Flowable
            .timer(updateIntervalMs, TimeUnit.MILLISECONDS)
            .repeat()
            .subscribeOnIO()
            .subscribeWithLogError { internalUpdate() }
            .let(gameUpdateDisposables::add)
    }

    abstract fun update(timeLeftMs: Long)

    open fun onTimeLeft() = finishGame()

    protected fun finishGame(
        onFinished: () -> Unit = { }
    ) {
        if (isFinished)
            return

        isFinished = true
        gameUpdateDisposables.clear()
        onFinished()
    }
}