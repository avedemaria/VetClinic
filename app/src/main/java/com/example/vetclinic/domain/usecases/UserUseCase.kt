package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.domain.repository.UserRepository
import jakarta.inject.Inject

class UserUseCase @Inject constructor(private val repository: UserRepository) {

    suspend fun addUserToSupabaseDb(user: User): Result<Unit> {
        return repository.addUserToSupabaseDb(user)
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