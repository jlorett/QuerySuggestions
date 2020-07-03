package com.joshualorett.querysuggestions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_search_result.view.*

/**
 * Represents a single search result item.
 * Created by Joshua on 4/14/2019.
 */
class SearchResultViewHolder(itemView: View, itemClickListener: ItemClickListener? = null) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.searchResultItem.setOnClickListener {
            itemClickListener?.onItemClick(adapterPosition)
        }
    }

    fun bind(result: String) {
        itemView.searchResultItem.text = result
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}