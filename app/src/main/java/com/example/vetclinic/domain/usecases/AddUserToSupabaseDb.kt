package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.User
import jakarta.inject.Inject

class AddUserToSupabaseDb @Inject constructor(private val repository: Repository) {

    suspend fun addUserToSupabaseDb(user: User): Result<Unit> {
       return repository.addUserToSupabaseDb(user)
    }

    suspend fun addPetToSupabaseDb (pet: Pet): Result<Unit> {
        return repository.addPetToSupabaseDb(pet)
    }
}