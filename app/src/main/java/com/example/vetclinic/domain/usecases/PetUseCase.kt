package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.repository.PetRepository
import jakarta.inject.Inject

class PetUseCase @Inject constructor(
    private val repository: PetRepository
) {

    suspend fun getPetsFromSupabaseDb (userId: String): Result<List<Pet>> {
        return repository.getPetsFromSupabaseDb(userId)
    }

    suspend fun getPetsFromRoom(userId: String): Result<List<Pet>> {
        return repository.getPetsFromRoom(userId)
    }


    suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit> {
        return repository.addPetToSupabaseDb(pet)
    }


    suspend fun addPetToRoom(pet: Pet): Result<Unit> {
        return repository.addPetToRoom(pet)
    }

    suspend fun deletePetFromSupabaseDb(pet: Pet): Result<Unit> {
        return repository.deletePetFromSupabaseDb(pet)
    }

    suspend fun updatePetInSupabaseDb(petId: String, updatedPet: Pet): Result<Unit> {
        return repository.updatePetInSupabaseDb(petId, updatedPet)
    }






}