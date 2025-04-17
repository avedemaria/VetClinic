package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.Repository
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject

class RegisterUserUseCase @Inject constructor(private val repository: Repository) {

    suspend fun registerUser(
        email: String,
        password: String,
    ): Result<UserSession> {
        return repository.registerUser(email, password)
    }
}