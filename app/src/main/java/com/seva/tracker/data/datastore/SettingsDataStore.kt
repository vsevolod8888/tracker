package com.seva.tracker.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val KEY_ROUTENAME = stringPreferencesKey("key_routename")
        val KEY_ROUTE_ID = longPreferencesKey("key_route_id")
        val KEY_THEME = booleanPreferencesKey("key_theme")
    }

    val routeId: Flow<Long> = dataStore.data
        .map { preferences ->
            try {
                preferences[PreferencesKeys.KEY_ROUTE_ID] ?: 0L
            } catch (e: ClassCastException) {
                val oldValue = preferences.asMap()[PreferencesKeys.KEY_ROUTE_ID] as? Int
                if (oldValue != null) {
                    dataStore.edit { it[PreferencesKeys.KEY_ROUTE_ID] = oldValue.toLong() }
                    oldValue.toLong()
                } else {
                    0L
                }
            }
        }
        .distinctUntilChanged()

    suspend fun saveRouteId(value: Long) {
        Log.d(TAG, "saveRouteId DataStore $value")
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_ROUTE_ID] = value
        }
    }

    val routenameDataStore: Flow<String> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.KEY_ROUTENAME].orEmpty() }
        .distinctUntilChanged()

    suspend fun saveRouteNameDataStore(value: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_ROUTENAME] = value
        }
    }

    val isThemeDarkDataStore: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.KEY_THEME] ?: false }
        .distinctUntilChanged()

    suspend fun saveIsThemeDarkDataStore(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_THEME] = value
        }
    }
    companion object{
        const val TAG = "SettingsDataStore"
    }
}
