package com.joshualorett.querysuggestions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * ViewModel to communicate between the search ui and repository.
 * Created by Joshua on 4/13/2019.
 */
class SearchViewModel(private val mockRepository: MockRepository) : ViewModel(), SearchEvents {
    private val compositeDisposable = CompositeDisposable()

    private val _results = MutableLiveData<List<String>>()
    val results : LiveData<List<String>> = _results

    private val _suggestions = MutableLiveData<List<String>>()
    val suggestions : LiveData<List<String>> = _suggestions

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val query = PublishSubject.create<String>()

    private val searchQuery = BehaviorSubject.create<String>()

    private val suggestionDisposable = query
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .debounce(300, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .switchMap { query ->
            mockRepository.getSuggestions(query)
        }
        .subscribe {  results ->
            _suggestions.postValue(results)
        }

    private val searchDisposable = searchQuery
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
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

    init {
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

    class SearchViewModelFactory(private val mockRepository: MockRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(mockRepository) as T
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