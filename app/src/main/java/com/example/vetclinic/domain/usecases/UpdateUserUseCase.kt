package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.Repository
import com.example.vetclinic.domain.entities.User
import jakarta.inject.Inject

class UpdateUserUseCase @Inject constructor(private val repository: Repository) {

    suspend fun updateUserInSupabaseDb(userId: String, updatedUser: User): Result<Unit> {
        return repository.updateUserInSupabaseDb(userId, updatedUser)
    }


    suspend fun updateUserInRoom(user: User): Result<Unit> {
        return repository.updateUserInRoom(user)
    }
}