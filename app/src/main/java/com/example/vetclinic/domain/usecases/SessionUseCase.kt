package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.repository.UserDataStore
import jakarta.inject.Inject

class SessionUseCase @Inject constructor(
    private val userDataStore: UserDataStore
) {

    suspend fun saveUserSession(userId: String, accessToken: String, refreshToken: String) {
        userDataStore.saveUserSession(userId, accessToken, refreshToken)
    }

    suspend fun saveUserRole(role: String) {
        userDataStore.saveUserRole(role)
    }

    suspend fun getUserId(): String? = userDataStore.getUserId()

    suspend fun getUserRole(): String? = userDataStore.getUserRole()

    suspend fun clearSession() {
        userDataStore.clearUserSession()
    }

    suspend fun getAccessToken (): String {
       return userDataStore.getAccessToken() ?: throw IllegalStateException("Access token is missing")
    }

    suspend fun getRefreshToken (): String {
       return userDataStore.getRefreshToken()?: throw IllegalStateException("Refresh token is missing")
    }
}