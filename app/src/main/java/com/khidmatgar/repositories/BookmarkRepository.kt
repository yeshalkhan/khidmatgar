package com.khidmatgar.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bookmarks")

class BookmarkRepository(private val context: Context) {

    private val dataStore: DataStore<Preferences> = context.dataStore

    private fun getBookmarksKey(userEmail: String) = stringSetPreferencesKey("${userEmail}_bookmarks")

    fun isBookmarkedLocally(userEmail: String, serviceProviderId: Int): Flow<Boolean> {
        val bookmarksKey = getBookmarksKey(userEmail)
        return dataStore.data
            .map { preferences ->
                val bookmarks = preferences[bookmarksKey] ?: emptySet()
                bookmarks.contains(serviceProviderId.toString())
            }
    }

    suspend fun saveBookmarkLocally(userEmail: String, serviceProviderId: Int) {
        val bookmarksKey = getBookmarksKey(userEmail)
        dataStore.edit { preferences ->
            val bookmarks = preferences[bookmarksKey]?.toMutableSet() ?: mutableSetOf()
            bookmarks.add(serviceProviderId.toString())
            preferences[bookmarksKey] = bookmarks
        }
    }

    suspend fun deleteBookmarkLocally(userEmail: String, serviceProviderId: Int) {
        val bookmarksKey = getBookmarksKey(userEmail)
        dataStore.edit { preferences ->
            val bookmarks = preferences[bookmarksKey]?.toMutableSet() ?: mutableSetOf()
            bookmarks.remove(serviceProviderId.toString())
            preferences[bookmarksKey] = bookmarks
        }
    }

    fun getBookmarkedServiceProviderIds(userEmail: String): Flow<Set<String>> {
        val bookmarksKey = getBookmarksKey(userEmail)
        return dataStore.data
            .map { preferences ->
                preferences[bookmarksKey] ?: emptySet()
            }
    }
}
