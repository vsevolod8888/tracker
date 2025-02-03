package com.seva.tracker.data.datastore

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        val KEY_USERNAME = stringPreferencesKey("key_username")
        val KEY_PHOTO_AVATAR = stringPreferencesKey("key_photo_avatar")
        val KEY_ENTER_COUNT = intPreferencesKey("key_enter_count")
    }

    var entercountDataStore: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_ENTER_COUNT] ?: 0
    }
    suspend fun saveEnterCount(value: Int){
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_ENTER_COUNT] = value
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

    var usernameDataStore: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_USERNAME] ?: ""
    }

    suspend fun saveUsernameDataStore(value: String){
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_USERNAME] = value
        }
    }
}