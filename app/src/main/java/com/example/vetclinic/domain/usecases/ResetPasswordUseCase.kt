package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.AuthRepository
import com.example.vetclinic.domain.interfaces.Repository
import jakarta.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val repository: AuthRepository) {


    suspend fun sendResetLink(email: String) {
        repository.resetPasswordWithEmail(email)
    }

    suspend fun updatePassword(newPassword: String, token: String): Result<Unit> {
        return repository.updatePassword(newPassword, token)
    }
}