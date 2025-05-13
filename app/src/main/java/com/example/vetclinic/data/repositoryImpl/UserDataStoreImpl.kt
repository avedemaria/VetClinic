package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.vetclinic.di.qualifiers.UserPrefs
import com.example.vetclinic.domain.repository.UserDataStore
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.first


@Singleton
class UserDataStoreImpl @Inject constructor(@UserPrefs private val dataStore: DataStore<Preferences>) :
    UserDataStore {


    override suspend fun saveUserSession(
        userId: String,
        accessToken: String,
        refreshToken: String,
    ) {
        Log.d("UserDataStore", "Saving userId: $userId")
        dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
            preferences[KEY_ACCESS_TOKEN] = accessToken
            preferences[KEY_REFRESH_TOKEN] = refreshToken

        }
    }

    override suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
        }
    }

    override suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
        }
    }


    override suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[KEY_REFRESH_TOKEN] = refreshToken
        }
    }


    override suspend fun saveUserRole(userRole: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_ROLE] = userRole
            Log.d("UserDataStore", "savedUserRole: $userRole")
        }
    }


    override suspend fun getUserId(): String? {
        return dataStore.data.first()[KEY_USER_ID]
    }

    override suspend fun getAccessToken(): String? {
        return dataStore.data.first()[KEY_ACCESS_TOKEN]
    }

    override suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[KEY_REFRESH_TOKEN]
    }


    override suspend fun getUserRole(): String? {
        return dataStore.data.first()[KEY_USER_ROLE]
    }

    override suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
            preferences.remove(KEY_USER_ROLE)
        }

    }


    companion object {
        private val KEY_USER_ID = stringPreferencesKey("key_user_id")
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
        private val KEY_USER_ROLE = stringPreferencesKey("key_user_role")
    }
}