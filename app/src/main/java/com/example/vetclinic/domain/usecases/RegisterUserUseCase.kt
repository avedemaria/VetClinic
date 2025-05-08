package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.repository.AuthRepository
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject

class RegisterUserUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend fun registerUser(
        email: String,
        password: String,
    ): Result<UserSession> {
        return repository.registerUser(email, password)
    }
}