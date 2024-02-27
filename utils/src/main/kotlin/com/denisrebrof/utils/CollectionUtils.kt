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
): Map<T, V> = associateByNotNull({ key -> key }, valueTransform)

fun <T> Iterable<T>.chunkedFixed(size: Int): List<List<T>> = chunked(size).filter { it.size == size }

public fun <T> Iterator<T>.another(n: Int): List<T> {
    require(n >= 0) { "Requested element count $n is less than zero." }
    if (n == 0) return emptyList()
    var count = 0
    val list = ArrayList<T>(n)
    for (item in this) {
        list.add(item)
        if (++count == n)
            break
    }
    return list
}

fun <A, B, R> Pair<A, B>.spread(f: (A, B) -> R) = f(first, second)
fun <A, B, R> Map.Entry<A, B>.spread(f: (A, B) -> R) = f(key, value)