package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.LocalDataCleaner
import com.example.vetclinic.domain.repository.AuthRepository
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository,
                                       private val localDataCleaner: LocalDataCleaner) {

    suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return authRepository.loginUser(email, password)
    }


    suspend fun logOut (): Result<Unit> {
        return authRepository.logOut()
    }

    suspend fun clearAllLocalData() {
        localDataCleaner.clearAllLocalData()
    }
}