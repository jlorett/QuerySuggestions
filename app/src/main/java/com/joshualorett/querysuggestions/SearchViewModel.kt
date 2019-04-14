package com.joshualorett.querysuggestions

import androidx.lifecycle.ViewModel

/**
 * ViewModel to communicate between the search ui and repository.
 * Created by Joshua on 4/13/2019.
 */
class SearchViewModel : ViewModel(), SearchEvents {
    override fun onQueryUpdated(query: String) {
        
    }
}

interface SearchEvents {
    fun onQueryUpdated(query: String)
}