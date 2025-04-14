package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.Repository
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.User
import jakarta.inject.Inject

class AddUserUseCase @Inject constructor(private val repository: Repository) {

    suspend fun addUserToSupabaseDb(user: User): Result<Unit> {
        return repository.addUserToSupabaseDb(user)
    }


    suspend fun addUserToRoom(user: User) {
        repository.addUserToRoom(user)
    }

    suspend fun addUserAndPetToRoom(user: User, pet: Pet) {
        repository.addUserAndPetToRoom(user, pet)
    }


}