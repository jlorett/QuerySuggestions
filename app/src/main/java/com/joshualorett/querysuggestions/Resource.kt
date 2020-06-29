package com.joshualorett.querysuggestions

/**
 * A class that indicates the status of a resource from a repository.
 * Created by Joshua on 6/29/2020.
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T): Resource<T>()
    data class Error(val error: Exception) : Resource<Exception>()
    object Loading : Resource<Nothing>()
    fun successful() : Boolean {
        return this is Success
    }
}

/**
 * Return data T if resource is successful otherwise null.
 */
val <T> Resource<T>.data: T?
    get() = (this as? Resource.Success)?.data

val Resource<Exception>.error: Exception?
    get() = (this as? Resource.Error)?.error