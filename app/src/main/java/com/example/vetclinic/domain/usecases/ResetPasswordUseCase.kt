package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.repository.AuthRepository
import jakarta.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val repository: AuthRepository) {


    suspend fun sendResetLink(email: String) {
        repository.resetPasswordWithEmail(email)
    }

    suspend fun updatePassword(newPassword: String, token: String, refreshToken:String): Result<Unit> {
        return repository.updatePassword(newPassword, token, refreshToken)
    }
}