package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.Repository
import com.example.vetclinic.domain.entities.Pet
import jakarta.inject.Inject

class AddPetUseCase @Inject constructor(private val repository: Repository) {


    suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit> {
        return repository.addPetToSupabaseDb(pet)
    }


    suspend fun addPetToRoom(pet: Pet): Result<Unit> {
        return repository.addPetToRoom(pet)
    }
}