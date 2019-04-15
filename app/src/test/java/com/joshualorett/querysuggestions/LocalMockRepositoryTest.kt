package com.joshualorett.querysuggestions

import org.junit.Test

import org.junit.Assert.*
import java.lang.IllegalArgumentException

/**
 * Created by Joshua on 4/15/2019.
 */
class LocalMockRepositoryTest {
    private val data = listOf("blah", "meh", "woot")

    @Test (expected = IllegalArgumentException::class)
    fun throwErrorIfNumberLessThanZero() {
        data.takeUntil(-1) { true }
    }

    @Test
    fun takeUntilReturnsEmptyOnZero() {
        assertTrue(data.takeUntil(0) { item -> item.contains("h") }.isEmpty())
    }

    @Test
    fun takeUntilReturnsEmptyIfNoMatch() {
        assertTrue(data.takeUntil(0) { item -> item.contains("zzzz") }.isEmpty())
    }

    @Test
    fun takeUntilFiltersElementByPredicate() {
        assertEquals(1, data.takeUntil(1) { item -> item == "woot" }.size)
    }

    @Test
    fun returnLessThanWantedIfListExhausted() {
        assertEquals(1, data.takeUntil(30) { item -> item == "blah" }.size)
    }

    @Test
    fun takeUntilReturnsNumberSpecified() {
        assertEquals(2, data.takeUntil(2) { item -> item.contains("h") }.size)
    }

    @Test
    fun takeUntilReturnsFirstElementsFound() {
        assertEquals(listOf("blah", "meh"), data.takeUntil(2) { item -> item.contains("h") })
    }
}