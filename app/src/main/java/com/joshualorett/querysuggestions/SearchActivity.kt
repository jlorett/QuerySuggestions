package com.joshualorett.querysuggestions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadingIndicator = findViewById<ProgressBar>(R.id.loading_indicator)
        val noSearchResults = findViewById<TextView>(R.id.no_search_results)
        val searchResults = findViewById<RecyclerView>(R.id.search_results)
        searchResults.layoutManager = LinearLayoutManager(this)
        searchResults.setHasFixedSize(true)
        val adapter = SearchResultAdapter()
        searchResults.adapter = adapter

        val searchViewModel = ViewModelProviders
            .of(this, SearchViewModel.SearchViewModelFactory(LocalMockRepository()))
            .get(SearchViewModel::class.java)

        val searchBar = findViewById<AppCompatAutoCompleteTextView>(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                noSearchResults.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchViewModel.onQueryUpdated(s?.toString() ?: "")
            }
        })
        searchBar.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchViewModel.search(searchBar.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        searchViewModel.suggestions.observe(this, Observer { suggestions ->
            val suggestionAdapter = ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, suggestions)
            searchBar.setAdapter(suggestionAdapter)
            suggestionAdapter.notifyDataSetChanged()
        })

        searchViewModel.loading.observe(this,  Observer { loading ->
            loadingIndicator.visibility = if(loading) View.VISIBLE else View.GONE
        })

        searchViewModel.results.observe(this, Observer { results ->
            noSearchResults.visibility = if(results.isEmpty()) View.VISIBLE else View.GONE
            adapter.updateSearchResults(results)
            adapter.notifyDataSetChanged()
        })
    }
}
