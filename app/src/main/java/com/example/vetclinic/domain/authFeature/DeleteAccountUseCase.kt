package com.example.vetclinic.domain.authFeature

import com.example.vetclinic.domain.interfaces.Repository
import jakarta.inject.Inject

class DeleteAccountUseCase @Inject constructor(private val repository: Repository) {

    suspend operator fun invoke(): Result<Unit> {
        return repository.deleteUserAccount()
    }
}