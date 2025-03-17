package com.example.vetclinic.domain.authFeature

import com.example.vetclinic.domain.Repository
import jakarta.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val repository: Repository) {


    suspend fun sendResetLink(email: String) {
        repository.resetPasswordWithEmail(email)
    }

    suspend fun updatePassword(newPassword: String, token: String, email: String): Result<Unit> {
        return repository.updatePassword(newPassword, token, email)
    }
}