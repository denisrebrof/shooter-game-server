package com.denisrebrof.springboottest.hideandseekgame.domain.core.model

import com.denisrebrof.springboottest.game.domain.model.Transform
import com.denisrebrof.springboottest.utils.subscribeOnIO
import com.denisrebrof.springboottest.utils.subscribeWithLogError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

class SleepPlace(
    private val timerMs: Long,
    val transform: Transform
) {
    private var sleepDisposable = Disposable.disposed()

    private var sleepingHiderId = 0L

    val sleepingHiderIdOrNull: Long?
        get() = when {
            sleepDisposable.isDisposed -> null
            else -> sleepingHiderId
        }

    val isOccupied: Boolean
        get() = sleepDisposable.isDisposed

    fun layDown(
        hiderId: Long,
        onFinished: () -> Unit
    ): Disposable {
        release()
        sleepingHiderId = hiderId
        return Completable
            .timer(timerMs, TimeUnit.MILLISECONDS)
            .doOnComplete { onFinished() }
            .subscribeOnIO()
            .subscribeWithLogError()
            .also(::sleepDisposable::set)
    }

    fun release() = sleepDisposable.dispose()
}