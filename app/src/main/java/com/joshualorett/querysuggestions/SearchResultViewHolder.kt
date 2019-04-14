package com.joshualorett.querysuggestions

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Represents a single search result item.
 * Created by Joshua on 4/14/2019.
 */
class SearchResultViewHolder(itemView: View, itemClickListener: ItemClickListener? = null) : RecyclerView.ViewHolder(itemView) {
    private val resultText = itemView.findViewById<TextView>(R.id.search_result_item_text)

    init {
        resultText.setOnClickListener {
            itemClickListener?.onItemClick(adapterPosition)
        }
    }

    fun bind(result: String) {
        resultText.text = result
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}