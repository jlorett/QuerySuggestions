package com.joshualorett.querysuggestions

import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

/**
 * Tests the [LocalMockRepository].
 * Created by Joshua on 4/15/2019.
 */
class LocalMockRepositoryTest {
    private val data = listOf("blah", "meh", "woot")
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setup() {
        testScheduler = TestScheduler()
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
        val testScheduler = TestScheduler()
        val repo = LocalMockRepository()
        val suggestions: TestObserver<List<String>> = repo.getSuggestions("antelope")
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        suggestions.assertValue(listOf("antelope"))
        suggestions.dispose()
    }

    @Test
    fun returnsMaxSuggestions() {
        val testScheduler = TestScheduler()
        val repo = LocalMockRepository(3, 0)
        val suggestions: TestObserver<List<String>> = repo.getSuggestions("a")
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        assertEquals(3, suggestions.values()[0].size)
        suggestions.dispose()
    }

    @Test
    fun searchReturns() {
        val testScheduler = TestScheduler()
        val repo = LocalMockRepository(3, 0)
        val results: TestObserver<List<String>> = repo.search("antelope")
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        results.assertValue(listOf("antelope"))
        results.dispose()
    }

    @Test
    fun returnsEmptyList() {
        val testScheduler = TestScheduler()
        val repo = LocalMockRepository(30, 0)
        val results: TestObserver<List<String>> = repo.search("")
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        results.assertValue(emptyList())
        results.dispose()
    }
}