package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.AuthRepository
import com.example.vetclinic.domain.interfaces.Repository
import jakarta.inject.Inject

class DeleteAccountUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend fun deleteAccount(): Result<Unit> {
        return repository.deleteUserAccount()
    }
}