package com.joshualorett.querysuggestions

import io.reactivex.Observable


/**
 * A mock repository to test searching.
 * Created by Joshua on 4/13/2019.
 */
interface MockRepository {
    fun search(query: String) : Observable<List<String>>
}

class LocalMockRepository : MockRepository {
    private val data = listOf("blah", "meh", "woot", "eh", "oh")

    override fun search(query: String) : Observable<List<String>> {
        if(query.isEmpty()) {
            return Observable.just(emptyList())
        }
        return Observable.just(data.filter { item -> item.startsWith(query, true) })
    }
}

