package com.denisrebrof.utils

fun <T, K : Any, V : Any> Iterable<T>.associateByNotNull(
    keySelector: (T) -> K?,
    valueTransform: (T) -> V?,
): Map<K, V> = buildMap {
    for (item in this@associateByNotNull) {
        val key = keySelector(item) ?: continue
        val value = valueTransform(item) ?: continue
        this[key] = value
    }
}

fun <T : Any, V : Any> Iterable<T>.associateWithNotNull(
    valueTransform: (T) -> V?,
): Map<T, V> = associateByNotNull({key -> key}, valueTransform)