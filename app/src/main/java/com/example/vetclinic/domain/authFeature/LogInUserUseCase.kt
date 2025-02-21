package com.example.vetclinic.domain.authFeature

import com.example.vetclinic.domain.Repository
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject

class LogInUserUseCase @Inject constructor(private val repository: Repository) {

    suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return repository.loginUser(email, password)
    }

    suspend fun checkUserSession (): Boolean {
        return repository.checkUserSession()
    }

    suspend fun logOut () {
        return repository.logOut()
    }
}