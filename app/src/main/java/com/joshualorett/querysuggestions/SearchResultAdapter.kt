package com.joshualorett.querysuggestions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Bind data to the search result list.
 * Created by Joshua on 4/14/2019.
 */
class SearchResultAdapter : RecyclerView.Adapter<SearchResultViewHolder>() {
    private var searchResults = listOf<String>()

    var itemClickListener: SearchResultViewHolder.ItemClickListener? = null

    fun updateSearchResults(data: List<String>) {
        searchResults = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_search_result, parent, false), itemClickListener)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(searchResults[position])
    }
}