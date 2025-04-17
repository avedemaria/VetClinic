package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.Repository
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(private val repository: Repository) {



    suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return repository.loginUser(email, password)
    }


    suspend fun logOut (): Result<Unit> {
        return repository.logOut()
    }

    suspend fun clearAllData() {
        repository.clearAllData()
    }
}