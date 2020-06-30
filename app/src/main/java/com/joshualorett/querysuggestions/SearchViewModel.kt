package com.joshualorett.querysuggestions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * ViewModel to communicate between the search ui and repository.
 * Created by Joshua on 4/13/2019.
 */
class SearchViewModel(schedulerProvider: SchedulerProvider,
                      private val mockRepository: MockRepository,
                      debounceTime: Long) : ViewModel(), SearchEvents {
    private val compositeDisposable = CompositeDisposable()

    private val _results = BehaviorSubject.create<Resource<List<String>>>()
    val results : Observable<Resource<List<String>>> = _results.hide()

    private val _suggestions = BehaviorSubject.create<Resource<List<String>>>()
    val suggestions: Observable<Resource<List<String>>> = _suggestions.hide()

    private val query = PublishSubject.create<String>()

    private val searchQuery = BehaviorSubject.create<String>()

    init {
        val suggestionDisposable = query
            .observeOn(schedulerProvider.ui)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .debounce(debounceTime, TimeUnit.MILLISECONDS, schedulerProvider.ui)
            .switchMap { query ->
                _suggestions.onNext(Resource.Loading)
                mockRepository.getSuggestions(query)
            }
            .subscribe {  results ->
                _suggestions.onNext(results)
            }

        val searchDisposable = searchQuery
            .observeOn(schedulerProvider.ui)
            .subscribeOn(schedulerProvider.io)
            .switchMap { query ->
                return@switchMap if(query.isEmpty()) {
                    Observable.fromCallable { Resource.Success(emptyList<String>()) }
                } else {
                    _results.onNext(Resource.Loading)
                    mockRepository.search(query)
                }
            }
            .subscribe { results ->
                _results.onNext(results)
            }
        compositeDisposable.addAll(suggestionDisposable, searchDisposable)
    }

    override fun updateQuery(query: String) {
        this.query.onNext(query)
    }

    override fun search(query: String) {
        searchQuery.onNext(query)
    }

    override fun cancelSearch() {
        searchQuery.onNext("")
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    class SearchViewModelFactory(private val schedulerProvider: SchedulerProvider,
                                 private val mockRepository: MockRepository,
                                 private val debounceTime: Long = 300L) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(schedulerProvider, mockRepository, debounceTime) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class.")
        }
    }
}

interface SearchEvents {
    fun updateQuery(query: String)
    fun search(query: String)
    fun cancelSearch()
}