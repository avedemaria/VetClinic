package com.example.vetclinic.domain.repository

import io.github.jan.supabase.auth.user.UserSession

interface AuthRepository {

    suspend fun loginUser(email: String, password: String): Result<UserSession>

    suspend fun registerUser(
        email: String,
        password: String,
    ): Result<UserSession>

    suspend fun logOut(): Result<Unit>

    suspend fun resetPasswordWithEmail(email: String): Result<Unit>

    suspend fun updatePassword(newPassword: String, token: String): Result<Unit>

    suspend fun deleteUserAccount (): Result<Unit>

    suspend fun checkUserSession(): Boolean

}