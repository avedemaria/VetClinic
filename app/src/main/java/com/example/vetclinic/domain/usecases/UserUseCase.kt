package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.domain.interfaces.Repository
import jakarta.inject.Inject

class UserUseCase @Inject constructor(private val repository: Repository) {

    suspend fun addUserToSupabaseDb(user: User): Result<Unit> {
        return repository.addUserToSupabaseDb(user)
    }


    suspend fun addUserToRoom(user: User) {
        repository.addUserToRoom(user)
    }

    suspend fun addUserAndPetToRoom(user: User, pet: Pet) {
        repository.addUserAndPetToRoom(user, pet)
    }


    suspend fun updateUserInSupabaseDb(userId: String, updatedUser: User): Result<Unit> {
        return repository.updateUserInSupabaseDb(userId, updatedUser)
    }


    suspend fun getUserFromSupabaseDb(userId: String): Result<User?> {
        return repository.getUserFromSupabaseDb(userId)

    }


    suspend fun getUserFromRoom(userId: String): Result<User> {
        return repository.getCurrentUserFromRoom(userId)
    }

}