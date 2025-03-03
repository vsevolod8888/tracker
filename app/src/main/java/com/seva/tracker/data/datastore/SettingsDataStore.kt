package com.seva.tracker.data.datastore

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(DelicateCoroutinesApi::class)
class SettingsDataStore @Inject constructor(@ApplicationContext private val context: Context){
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: SettingsDataStore? = null
    }

    private object PreferencesKeys{
        val KEY_ROUTENAME = stringPreferencesKey("key_routename")
        val KEY_PHOTO_AVATAR = stringPreferencesKey("key_photo_avatar")
        val KEY_ROUTE_ID = longPreferencesKey("key_route_id")
        val KEY_THEME = booleanPreferencesKey("key_theme")

    }

    var route_id: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_ROUTE_ID] ?: 0L
    }
    suspend fun saveRouteId(value: Long){
        Log.d("zzz", "saveRouteId DataStore ${value}")
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_ROUTE_ID] = value
        }
    }

    var photoAvatarDataStore: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_PHOTO_AVATAR] ?: ""
    }
    suspend fun savePhotoAvatarDataStore(value: String){
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_PHOTO_AVATAR] = value
        }
    }

    var routenameDataStore: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_ROUTENAME] ?: ""
    }

    suspend fun saveRouteNameDataStore(value: String){
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_ROUTENAME] = value
        }
    }

    var isThemeDarkDataStore: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_THEME] ?: false
    }
    suspend fun saveIsThemeDarkDataStore(value: Boolean){
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_THEME] = value
        }
    }
}