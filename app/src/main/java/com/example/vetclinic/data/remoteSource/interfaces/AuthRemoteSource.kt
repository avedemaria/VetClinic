package com.example.vetclinic.data.remoteSource.interfaces

import io.github.jan.supabase.auth.user.UserSession

interface AuthRemoteSource {

    suspend fun loginUser(email: String, password: String): Result<UserSession>

    suspend fun registerUser(
        email: String,
        password: String,
    ): Result<UserSession>

    suspend fun logOut(): Result<Unit>

    suspend fun resetPasswordWithEmail(email: String): Result<Unit>

    suspend fun updatePassword(newPassword: String, token: String, refreshToken:String): Result<Unit>

    suspend fun deleteUserAccount (): Result<Unit>

}