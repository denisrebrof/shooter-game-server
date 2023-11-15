package com.denisrebrof.utils

import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.DisposableBean

abstract class DisposableService: DisposableBean {
    abstract val handler: Disposable

    override fun destroy() = handler.dispose()
}