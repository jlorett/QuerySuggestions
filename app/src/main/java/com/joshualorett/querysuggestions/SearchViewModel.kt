package com.joshualorett.querysuggestions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _results = MutableLiveData<List<String>>()
    val results : LiveData<List<String>> = _results

    private val _suggestions = MutableLiveData<List<String>>()
    val suggestions : LiveData<List<String>> = _suggestions

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val query = PublishSubject.create<String>()

    private val searchQuery = BehaviorSubject.create<String>()

    init {
        val suggestionDisposable = query
            .observeOn(schedulerProvider.ui)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .debounce(debounceTime, TimeUnit.MILLISECONDS, schedulerProvider.ui)
            .switchMap { query ->
                mockRepository.getSuggestions(query)
            }
            .subscribe {  results ->
                _suggestions.postValue(results)
            }

        val searchDisposable = searchQuery
            .observeOn(schedulerProvider.ui)
            .subscribeOn(schedulerProvider.io)
            .switchMap { query ->
                _suggestions.postValue(emptyList())
                return@switchMap if(query.isEmpty()) {
                    _loading.postValue(false)
                    Observable.fromCallable<List<String>> { emptyList() }
                } else {
                    _loading.postValue(true)
                    mockRepository.search(query)
                }
            }
            .subscribe { results ->
                _loading.postValue(false)
                _results.postValue(results)
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