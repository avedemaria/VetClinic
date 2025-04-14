package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.Repository
import com.example.vetclinic.domain.entities.Pet
import jakarta.inject.Inject

class GetPetsUseCase @Inject constructor(private val repository: Repository) {

    suspend fun getPetsFromSupabaseDb (userId: String): Result<List<Pet>> {
        return repository.getPetsFromSupabaseDb(userId)
    }

    suspend fun getPetsFromRoom(userId: String): Result<List<Pet>> {
        return repository.getPetsFromRoom(userId)
    }

}