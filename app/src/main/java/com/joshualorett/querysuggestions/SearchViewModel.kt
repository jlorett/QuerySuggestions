package com.joshualorett.querysuggestions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * ViewModel to communicate between the search ui and repository.
 * Created by Joshua on 4/13/2019.
 */
class SearchViewModel(private val mockRepository: MockRepository) : ViewModel(), SearchEvents {
    private var suggestionDisposable: Disposable? = null
    private var searchDisposable: Disposable? = null

    private val _results = MutableLiveData<List<String>>()
    val results : LiveData<List<String>> = _results

    private val _suggestions = MutableLiveData<List<String>>()
    val suggestions : LiveData<List<String>> = _suggestions

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val query = PublishSubject.create<String>()

    init {
        suggestionDisposable = query
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
    }

    override fun onQueryUpdated(query: String) {
        if(query.isNotEmpty()) {
            this.query.onNext(query)
        }
    }

    override fun search(query: String) {
        if(query.isNotEmpty()) {
            _suggestions.postValue(emptyList())
            _loading.postValue(true)
            searchDisposable = mockRepository.search(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { results ->
                    _loading.postValue(false)
                    _results.postValue(results)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        suggestionDisposable?.dispose()
        searchDisposable?.dispose()
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
    fun onQueryUpdated(query: String)
    fun search(query: String)
}