package com.denisrebrof.utils

fun List<Long>.getSimpleHash(): Long {
    var hash = 17L
    for (number in this)
        hash = hash * 19 + number

    return hash
}
