package com.joshualorett.querysuggestions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchViewModel = ViewModelProviders
            .of(this, SearchViewModel.SearchViewModelFactory(LocalMockRepository()))
            .get(SearchViewModel::class.java)

        val searchBar = findViewById<AppCompatAutoCompleteTextView>(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchViewModel.onQueryUpdated(s?.toString() ?: "")
            }
        })

        searchViewModel.suggestions.observe(this, Observer { suggestions ->
            Log.d("logger", suggestions.toString())
        })
    }
}
