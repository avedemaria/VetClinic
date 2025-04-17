package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.Repository
import jakarta.inject.Inject

class DeleteAccountUseCase @Inject constructor(private val repository: Repository) {

    suspend fun deleteAccount(): Result<Unit> {
        return repository.deleteUserAccount()
    }
}