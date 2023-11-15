package com.denisrebrof.utils

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

inline fun <reified T> Maybe<T>.subscribeWithLogError(noinline consumer: (T) -> Unit): Disposable {
    return doOnError {
        handleThrowable(it)
    }
        .onErrorComplete()
        .subscribe(consumer)
}

inline fun <reified T : Any> Single<T>.subscribeWithLogError(noinline consumer: (T) -> Unit): Disposable {
    return this.subscribe(consumer) {
        handleThrowable(it)
    }
}

inline fun <reified T : Any> Flowable<T>.subscribeWithLogError(): Disposable {
    return this.subscribe({ }, { handleThrowable(it) })
}

inline fun <reified T : Any> Flowable<T>.subscribeWithLogError(noinline consumer: (T) -> Unit): Disposable {
    return this.subscribe(consumer) {
        handleThrowable(it)
    }
}

inline fun <reified T : Any> Observable<T>.subscribeWithLogError(noinline consumer: (T) -> Unit = {}): Disposable {
    return this.subscribe(consumer) {
        handleThrowable(it)
    }
}

fun Completable.subscribeWithLogError(): Disposable {
    return this.subscribe({}, ::handleThrowable)
}

fun handleThrowable(th: Throwable) {
    println(th)
}

fun <ValueType : Any> Flowable<ValueType>.subscribeOnIO(): Flowable<ValueType> {
    return this.subscribeOn(Schedulers.io())
}

fun <ValueType : Any> Single<ValueType>.subscribeOnIO(): Single<ValueType> {
    return this.subscribeOn(Schedulers.io())
}

fun <ValueType : Any> Observable<ValueType>.subscribeOnIO(): Observable<ValueType> {
    return this.subscribeOn(Schedulers.io())
}

fun <ValueType : Any> Maybe<ValueType>.subscribeOnIO(): Maybe<ValueType> {
    return this.subscribeOn(Schedulers.io())
}

fun Completable.subscribeOnIO(): Completable = this.subscribeOn(Schedulers.io())


inline fun <reified ValueType : Any> Flowable<ValueType>.subscribeDefault(
    noinline consumer: (ValueType) -> Unit = { Unit },
): Disposable = this
    .subscribeOnIO()
    .subscribeWithLogError(consumer)

inline fun <reified ValueType : Any> Observable<ValueType>.subscribeDefault(
    noinline consumer: (ValueType) -> Unit = { Unit },
): Disposable = this
    .subscribeOnIO()
    .subscribeWithLogError(consumer)

inline fun <reified ValueType : Any> Maybe<ValueType>.subscribeDefault(
    noinline consumer: (ValueType) -> Unit = { Unit },
): Disposable = this
    .subscribeOnIO()
    .subscribeWithLogError(consumer)

inline fun <reified ValueType : Any> Single<ValueType>.subscribeDefault(
    noinline consumer: (ValueType) -> Unit = { Unit },
): Disposable = this
    .subscribeOnIO()
    .subscribeWithLogError(consumer)

fun Completable.subscribeDefault(
): Disposable = this
    .subscribeOnIO()
    .subscribeWithLogError()

fun <T : Any> Flowable<T>.filterNot(condition: (T) -> Boolean): Flowable<T> {
    return filter { !condition(it) }
}

fun <T : Any> Maybe<T>.filterNot(condition: (T) -> Boolean): Maybe<T> {
    return filter { !condition(it) }
}

fun Flowable<Boolean>.filterIsTrue(): Flowable<Boolean> {
    return filter { item -> item }
}

fun Flowable<Boolean>.filterIsFalse(): Flowable<Boolean> {
    return filter { item -> !item }
}

fun Maybe<Boolean>.filterIsTrue(): Maybe<Boolean> {
    return filter { item -> item }
}

fun Maybe<Boolean>.filterIsFalse(): Maybe<Boolean> {
    return filter { item -> !item }
}

inline fun <T : Any, S : Any> Flowable<T>.mapNotNull(
    crossinline transform: (T) -> S?,
): Flowable<S> = this.flatMap {
    return@flatMap when (val result = transform(it)) {
        null -> Flowable.empty()
        else -> Flowable.just(result)
    }
}

inline fun <T : Any, S : Any> Maybe<T>.mapNotNull(
    crossinline transform: (T) -> S?,
): Maybe<S> = this.flatMap {
    return@flatMap when (val result = transform(it)) {
        null -> Maybe.empty()
        else -> Maybe.just(result)
    }
}