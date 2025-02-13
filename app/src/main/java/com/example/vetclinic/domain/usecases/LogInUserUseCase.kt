package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.google.firebase.auth.AuthResult
import jakarta.inject.Inject

class LogInUserUseCase @Inject constructor(private val authRepository: Repository) {

    suspend fun loginUser(email: String, password: String): AuthResult {
        return authRepository.loginUser(email, password)
    }
}