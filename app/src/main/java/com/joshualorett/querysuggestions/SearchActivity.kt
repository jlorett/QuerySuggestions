package com.joshualorett.querysuggestions

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.widget_searchbar.*

class SearchActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchResults.layoutManager = LinearLayoutManager(this)
        searchResults.setHasFixedSize(true)
        val adapter = SearchResultAdapter()
        searchResults.adapter = adapter
        val schedulerProvider = AppSchedulerProvider()
        val searchViewModel = ViewModelProvider(this,
            SearchViewModel.SearchViewModelFactory(schedulerProvider, LocalMockRepository(schedulerProvider, 5, 3)))
            .get(SearchViewModel::class.java)
        clearQuery.setOnClickListener {
            searchViewModel.clear()
            searchBar.setText("")
            loadingIndicator.visibility = View.GONE
            noSearchResults.visibility = View.GONE
        }
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchViewModel.updateQuery(s?.toString() ?: "")
            }
        })
        searchBar.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                noSearchResults.visibility = View.GONE
                searchViewModel.search(searchBar.text.toString())
                closeKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        val suggestionAdapter = ArrayAdapter(this, android.R.layout.simple_selectable_list_item, emptyList<String>())
        searchBar.setAdapter(suggestionAdapter)
        val suggestionDisposable = searchViewModel.suggestions
            .observeOn(schedulerProvider.ui)
            .subscribeOn(schedulerProvider.ui)
            .subscribe{ suggestions ->
            when(suggestions) {
                is Resource.Success -> {
                    val data = suggestions.data
                    suggestionAdapter.clear()
                    suggestionAdapter.addAll(data)
                    suggestionAdapter.notifyDataSetChanged()
                }
            }
        }
        val resultDisposable = searchViewModel.results
            .observeOn(schedulerProvider.ui)
            .subscribeOn(schedulerProvider.ui)
            .subscribe { results ->
            when(results) {
                is Resource.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val data = results.data
                    noSearchResults.visibility = if(data.isEmpty() && searchBar.text.isNotEmpty()) View.VISIBLE else View.GONE
                    adapter.updateSearchResults(data)
                    adapter.notifyDataSetChanged()
                }
                is Resource.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
            }
        }
        compositeDisposable.addAll(suggestionDisposable, resultDisposable)
    }

    private fun closeKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(searchBar.windowToken, HIDE_NOT_ALWAYS)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
