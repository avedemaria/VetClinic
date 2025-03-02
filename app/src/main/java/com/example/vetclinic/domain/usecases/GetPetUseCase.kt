package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.Pet
import jakarta.inject.Inject

class GetPetUseCase @Inject constructor(private val repository: Repository) {

    suspend fun getPetFromRoom(userId: String): Result<List<Pet>> {
        return repository.getPetsFromRoom(userId)
    }

}