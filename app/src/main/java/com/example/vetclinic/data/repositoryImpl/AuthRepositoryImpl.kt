package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout

class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val supabaseApiService: SupabaseApiService,
) : AuthRepository {

    override suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return try {

            val deferred = CompletableDeferred<UserSession>()

            Email.login(
                supabaseClient,
                onSuccess = { session ->
                    deferred.complete(session)
                }
            ) {
                this.email = email
                this.password = password
            }
            Result.success(deferred.await())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun registerUser(email: String, password: String): Result<UserSession> {
        return try {

            val deferred = CompletableDeferred<UserSession>()

            Email.signUp(
                supabaseClient,
                onSuccess = { session ->
                    Log.d(TAG, "registerUser onSuccess")
                    deferred.complete(session)
                }
            ) {
                this.email = email
                this.password = password
            }

            Result.success(withTimeout(10_000) { deferred.await() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun logOut(): Result<Unit> = kotlin.runCatching {

        supabaseClient.auth.signOut()
        Log.d(TAG, "User has been signed out successfully")
        Unit
    }
        .onFailure { e ->
            Log.d(TAG, "Error while signing out user")
        }

    override suspend fun resetPasswordWithEmail(email: String): Result<Unit> =
        kotlin.runCatching {
            val resetPasswordUrl = "vetclinic://reset-password"
            supabaseClient.auth.resetPasswordForEmail(email, resetPasswordUrl)
            Log.d(TAG, "The reset password link has been sent to $email")
            Unit
        }
            .onFailure { e ->
                Log.d(TAG, "Error while sending the reset link to $email")
            }


    override suspend fun updatePassword(newPassword: String, token: String)
            : Result<Unit> =
        kotlin.runCatching {

            if (token.isBlank()) {
                throw IllegalArgumentException("Token is empty")
            }

//
//            val email = decodeJwtAndGetEmail(token) ?: ""
//
//            supabaseClient.auth.verifyEmailOtp(
//                email = email,
//                token = token,
//                type = OtpType.Email.RECOVERY
//            )

            supabaseClient.auth.updateUser {
                password = newPassword
            }
            Log.d(
                TAG,
                "The password has been successfully updated for " +
                        "${supabaseClient.auth.currentUserOrNull()}"
            )
            Unit
        }
            .onFailure { e ->
                Log.d(TAG, "Error while updating password $e")
            }


    private fun decodeJwtAndGetEmail(token: String): String? {
        try {
            val payload = token.split(".")[1]
            val decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE)
            val decodedPayload = String(decodedBytes)

            // Use a JSON parser to extract the email
            val jsonObject = org.json.JSONObject(decodedPayload)
            return jsonObject.optString("email")
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding JWT", e)
            return null
        }
    }

    override suspend fun deleteUserAccount(): Result<Unit> = kotlin.runCatching {
        val response = supabaseApiService.deleteUser()
        if (response.isSuccessful) {
            logOut().getOrThrow()
        } else {
            throw Exception(
                "Failed to delete user: ${response.code()} ${
                    response.errorBody()?.string()
                }"
            )
        }
    }.onFailure { e ->
        Log.d(TAG, "Error while deleting user account $e")
    }



    override suspend fun checkUserSession(): Boolean {
        return try {
            supabaseClient.auth.currentUserOrNull() != null
        } catch (e: Exception) {
            false
        }
    }


    companion object {
        private const val TAG = "AuthRepository"
    }
}