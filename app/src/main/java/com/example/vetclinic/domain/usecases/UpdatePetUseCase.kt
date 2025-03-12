package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.Pet
import jakarta.inject.Inject

class UpdatePetUseCase @Inject constructor(private val repository: Repository) {

    suspend fun updatePetInSupabaseDb(petId: String, updatedPet: Pet): Result<Unit> {
        return repository.updatePetInSupabaseDb(petId, updatedPet)
    }


}