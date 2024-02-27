package com.denisrebrof.shooter.domain.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BotsVisibilityMaskTest {

    @Test
    fun getVisibility() {
        val mask = BotsVisibilityMask(
            ids = setOf(0L, 1L, 2L),
            matrix = listOf(false, true, false)
        )
        assertFalse { mask.getVisibility(0L, 1L) }
        assertFalse { mask.getVisibility(1L, 0L) }

        assertFalse { mask.getVisibility(1L, 2L) }
        assertFalse { mask.getVisibility(2L, 1L) }

        assertTrue { mask.getVisibility(0L, 2L) }
        assertTrue { mask.getVisibility(2L, 0L) }
    }

    @Test
    fun update() {
    }

    @Test
    fun append() {
        var mask = BotsVisibilityMask(
            ids = setOf(0L, 1L, 3L),
            matrix = listOf(false, true, false)
        )

        mask = mask.append(2L)

        //Assert original kept
        assertFalse { mask.getVisibility(0L, 1L) }
        assertFalse { mask.getVisibility(1L, 0L) }

        assertFalse { mask.getVisibility(1L, 3L) }
        assertFalse { mask.getVisibility(3L, 1L) }

        assertTrue { mask.getVisibility(0L, 3L) }
        assertTrue { mask.getVisibility(3L, 0L) }

        //Assert all new ones are false
        assertFalse { mask.getVisibility(2L, 0L) }
        assertFalse { mask.getVisibility(2L, 1L) }
        assertFalse { mask.getVisibility(2L, 3L) }
    }

    @Test
    fun exclude() {
        var mask = BotsVisibilityMask(
            ids = setOf(0L, 1L, 2L),
            matrix = listOf(true, false, true)
        )

        mask = mask.exclude(0L)

        assertTrue { mask.getVisibility(1L, 2L) }
    }
}