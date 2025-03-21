package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.Pet
import jakarta.inject.Inject

class DeletePetUseCase @Inject constructor(
    private val repository: Repository
) {

    suspend fun deletePetFromSupabaseDb(pet: Pet): Result<Unit> {
        return repository.deletePetFromSupabaseDb(pet)
    }

}