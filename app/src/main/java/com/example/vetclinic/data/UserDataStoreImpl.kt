package com.example.vetclinic.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.vetclinic.domain.UserDataStore
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking


@Singleton
class UserDataStoreImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    UserDataStore {


    override suspend fun saveUserSession(userId: String, accessToken: String) {
        Log.d("UserDataStore", "Saving userId: $userId")
        dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
            preferences[KEY_ACCESS_TOKEN] = accessToken

        }
    }

    override suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
        }
    }

    override suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = token
        }
    }

    override suspend fun getUserId(): String? {
        return dataStore.data.first()[KEY_USER_ID]
    }

    override suspend fun getAccessToken(): String? {
        return dataStore.data.first()[KEY_ACCESS_TOKEN]
    }


    override suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_ACCESS_TOKEN)
        }

    }

    override val userIdFlow: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_ID]
        }

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("key_user_id")
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("key_access_token")
    }
}