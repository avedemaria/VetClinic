package com.example.vetclinic.domain.usecases

import android.net.Uri
import android.util.Log
import com.example.vetclinic.domain.repository.UserDataStore
import jakarta.inject.Inject

class HandleDeepLinkUseCase @Inject constructor(
    private val userDataStore: UserDataStore
) {
    suspend fun handleDeepLink(uri: Uri): Result<Unit> {
        return try {
            if (uri.host == "reset-password") {
                val fullUri = uri.toString()

                val token = fullUri.substringAfter("#access_token=")
                    .substringBefore("&")
                val refreshToken = fullUri.substringAfter("refresh_token=")
                    .substringBefore("&")

                Log.d(TAG, "Extracted token: $token")
                Log.d(TAG, "Extracted refresh token: $refreshToken")

                if (token.isNotBlank() && refreshToken.isNotBlank()) {
                    saveTokensToDataStore(token, refreshToken)
                    Result.success(Unit)
                } else {
                    Result.failure(IllegalArgumentException("Invalid tokens"))
                }
            } else {
                Result.failure(IllegalArgumentException("Invalid host"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private suspend fun saveTokensToDataStore(token: String, refreshToken: String) {
        val userId = userDataStore.getUserId()?:""
        userDataStore.saveUserSession(userId = userId, accessToken = token, refreshToken = refreshToken)
    }

    companion object {
        private const val TAG = "HandleDeepLinkUseCase"
    }
}