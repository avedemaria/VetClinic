package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
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