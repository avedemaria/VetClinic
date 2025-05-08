package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.repository.AuthRepository
import com.example.vetclinic.domain.repository.Repository
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository,
                                       private val repository: Repository) {



    suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return authRepository.loginUser(email, password)
    }


    suspend fun logOut (): Result<Unit> {
        return authRepository.logOut()
    }

    suspend fun clearAllData() {
        repository.clearAllData()
    }
}