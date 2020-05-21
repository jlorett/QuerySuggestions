package com.joshualorett.querysuggestions

import io.reactivex.observers.TestObserver
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.lang.IllegalArgumentException

/**
 * Tests the [LocalMockRepository].
 * Created by Joshua on 4/15/2019.
 */
class LocalMockRepositoryTest {
    private val data = listOf("blah", "meh", "woot")
    private lateinit var testSchedulerProvider: TestSchedulerProvider

    @Before
    fun setup() {
        testSchedulerProvider = TestSchedulerProvider()
    }

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

    @Test
    fun returnsSuggestions() {
        val repo = LocalMockRepository(testSchedulerProvider, 3, 0)
        val suggestions: TestObserver<List<String>> = repo.getSuggestions("antelope")
            .subscribeOn(testSchedulerProvider.ui)
            .observeOn(testSchedulerProvider.io)
            .test()
        suggestions.assertValue(listOf("antelope"))
        suggestions.dispose()
    }

    @Test
    fun returnsMaxSuggestions() {
        val repo = LocalMockRepository(testSchedulerProvider, 3, 0)
        val suggestions: TestObserver<List<String>> = repo.getSuggestions("a")
            .subscribeOn(testSchedulerProvider.ui)
            .observeOn(testSchedulerProvider.io)
            .test()
        assertEquals(3, suggestions.values()[0].size)
        suggestions.dispose()
    }

    @Test
    fun searchReturns() {
        val repo = LocalMockRepository(testSchedulerProvider, 3, 0)
        val results: TestObserver<List<String>> = repo.search("antelope")
            .subscribeOn(testSchedulerProvider.ui)
            .observeOn(testSchedulerProvider.io)
            .test()
        results.assertValue(listOf("antelope"))
        results.dispose()
    }

    @Test
    fun returnsEmptyList() {
        val repo = LocalMockRepository(testSchedulerProvider, 30, 0)
        val results: TestObserver<List<String>> = repo.search("")
            .subscribeOn(testSchedulerProvider.ui)
            .observeOn(testSchedulerProvider.io)
            .test()
        results.assertValue(emptyList())
        results.dispose()
    }
}