package com.example.vetclinic.data.repositoryImpl

import com.example.vetclinic.data.remoteSource.interfaces.AuthRemoteSource
import com.example.vetclinic.domain.repository.AuthRepository
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject
import timber.log.Timber

class AuthRepositoryImpl @Inject constructor(
    private val remoteSource: AuthRemoteSource
) : AuthRepository {

    override suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return remoteSource.loginUser(email, password)
    }

    override suspend fun registerUser(email: String, password: String): Result<UserSession> {
        return remoteSource.registerUser(email, password)
    }


    override suspend fun logOut(): Result<Unit> =
        remoteSource.logOut()
            .onSuccess { Timber.tag(TAG).d("User signed out") }
            .onFailure { Timber.tag(TAG).e("Sign out failed: ${it.message}") }


    override suspend fun resetPasswordWithEmail(email: String): Result<Unit> =
        remoteSource.resetPasswordWithEmail(email)


    override suspend fun updatePassword(newPassword: String, token: String, refreshToken: String)
            : Result<Unit> =
        remoteSource.updatePassword(newPassword, token, refreshToken)
            .onFailure { e ->
                Timber.tag(TAG).e(e, "Failed to update password: ${e.message}")
            }


    override suspend fun deleteUserAccount(): Result<Unit> =
        remoteSource.deleteUserAccount()
            .onSuccess {
                Timber.tag(TAG).d("User account deleted and logged out successfully")
            }
            .onFailure { e ->
                Timber.tag(TAG).e(e, "Error while deleting user account: ${e.message}")
            }


    companion object {
        private const val TAG = "AuthRepository"
    }
}