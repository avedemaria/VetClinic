package com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl

import com.example.vetclinic.data.remoteSource.interfaces.AuthRemoteSource
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject

class AuthRemoteSourceImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val supabaseClient: SupabaseClient,
) : AuthRemoteSource {

    override suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return runCatching {
            var session: UserSession? = null

            Email.login(
                supabaseClient,
                onSuccess = { userSession ->
                    session = userSession
                }
            ) {
                this.email = email
                this.password = password
            }

            requireNotNull(session) { "User session is null after sign in" }
        }
    }

    override suspend fun registerUser(email: String, password: String): Result<UserSession> {
        return runCatching {
            var session: UserSession? = null

            Email.signUp(
                supabaseClient,
                onSuccess = { userSession ->
                    session = userSession
                }
            ) {
                this.email = email
                this.password = password
            }

            requireNotNull(session) { "User session is null after sign up" }
        }
    }


    override suspend fun logOut(): Result<Unit> {
        return runCatching {
            supabaseClient.auth.signOut()
        }
    }


    override suspend fun resetPasswordWithEmail(email: String): Result<Unit> =
        runCatching {
            val resetPasswordUrl = "vetclinic://reset-password"
            supabaseClient.auth.resetPasswordForEmail(email, resetPasswordUrl)
        }


    override suspend fun updatePassword(
        newPassword: String,
        token: String,
        refreshToken: String
    ): Result<Unit> = runCatching {
        require(token.isNotBlank()) { "Token is empty" }

        supabaseClient.auth.importSession(
            session = UserSession(
                accessToken = token,
                refreshToken = refreshToken,
                expiresIn = 2000,
                tokenType = "Bearer",
                user = null
            )
        )
        supabaseClient.auth.updateUser(updateCurrentUser = false) {
            password = newPassword
        }
    }

    override suspend fun deleteUserAccount(): Result<Unit> = runCatching {
        val response = supabaseApiService.deleteUser()

        if (response.isSuccessful) {
            logOut().getOrThrow()
        } else {
            val errorMessage = response.errorBody()?.string()
            throw Exception("Failed to delete user: ${response.code()} - $errorMessage")
        }
    }


}