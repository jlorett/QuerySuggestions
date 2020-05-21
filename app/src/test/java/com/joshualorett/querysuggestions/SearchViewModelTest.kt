package com.joshualorett.querysuggestions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * Tests the [SearchViewModel]
 * Created by Joshua on 5/17/2020.
 */
class SearchViewModelTest {
    @get:Rule
    val test = InstantTaskExecutorRule()

    private val mockRepo = LocalMockRepository(3, 0)
    private val testSchedulerProvider = TestSchedulerProvider()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        viewModel = SearchViewModel.SearchViewModelFactory(testSchedulerProvider, mockRepo, 0).create(SearchViewModel::class.java)
    }

    @Test
    fun emptyQueryReturnsNoSuggestions() {
        viewModel.updateQuery("")
        assertTrue(viewModel.suggestions.value?.isEmpty() == true)
    }

    @Test
    fun returnsSuggestions() {
        viewModel.updateQuery("ant")
        assertTrue(viewModel.suggestions.value?.size ?: 0 > 0)
    }

    @Test
    fun emptySearchReturnsNoResults() {
        viewModel.search("")
        assertTrue(viewModel.results.value?.isEmpty() == true)
    }

    @Test
    fun returnsResults() {
        viewModel.search("ant")
        assertTrue(viewModel.results.value?.size ?: 0 > 0)
    }
}