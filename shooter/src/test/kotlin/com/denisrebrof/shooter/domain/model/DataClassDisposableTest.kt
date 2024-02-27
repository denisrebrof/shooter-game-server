package com.denisrebrof.shooter.domain.model

import io.reactivex.rxjava3.disposables.Disposable
import org.junit.jupiter.api.Test

class DataClassDisposableTest {

    private data class DisposableHandler(
        val a: Int,
        val disposable: Disposable
    )

    @Test
    fun testDataClassDisposable() {
        val disposableSource = Disposable.empty()
        assert(!disposableSource.isDisposed)

        val first = DisposableHandler(0, disposableSource)
        val second = first.copy(a = 10)

        assert(!disposableSource.isDisposed)
        second.disposable.dispose()

        assert(disposableSource.isDisposed)
        assert(first.disposable.isDisposed)
        assert(second.disposable.isDisposed)
    }
}