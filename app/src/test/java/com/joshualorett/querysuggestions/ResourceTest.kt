package com.joshualorett.querysuggestions

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [Resource].
 * Created by Joshua on 6/29/2020.
 */
class ResourceTest {
    @Test
    fun errorReturnsException() {
        val error = IllegalArgumentException()
        val resource = Resource.Error(error)
        assertEquals(error, resource.error)
    }

    @Test
    fun errorReturnsNullIfNotError() {
        val resource = Resource.Loading
        assertNull(resource.error)
    }

    @Test
    fun dataReturnsValue() {
        val resource = Resource.Success(20);
        assertEquals(20, resource.data)
    }

    @Test
    fun dataReturnsNullIfNotSuccess() {
        val resource = Resource.Error(NullPointerException())
        assertNull(resource.data)
    }

    @Test
    fun resourceIsSuccessful() {
        assertTrue(Resource.Success(1).successful())
    }

    @Test
    fun resourceIsNotSuccessfulOnLoading() {
        assertFalse(Resource.Loading.successful())
    }

    @Test
    fun resourceIsNotSuccessfulOnError() {
        assertFalse(Resource.Error(Exception()).successful())
    }
}