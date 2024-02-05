package com.denisrebrof.games

import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue

class TransformTests {

    @Test
    fun translationTest() {
        val transform = Transform(0.5f, 0.0f, 0.5f, 0f)
        val target = Transform(1.5f, 0.0f, 0.5f, 0f)
        val result = transform.translateTo(target, 1f)
        assert(result.x.minus(target.x).absoluteValue < 0.01f)
        assert(result.y.minus(target.y).absoluteValue < 0.01f)
        assert(result.z.minus(target.z).absoluteValue < 0.01f)
    }
}