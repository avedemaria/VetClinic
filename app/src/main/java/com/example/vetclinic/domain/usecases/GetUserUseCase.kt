package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.Repository
import com.example.vetclinic.domain.entities.User
import jakarta.inject.Inject

class GetUserUseCase @Inject constructor(private val repository: Repository) {


    suspend fun getUserFromSupabaseDb(userId: String): Result<User?> {
        return repository.getUserFromSupabaseDb(userId)

    }


    suspend fun getUserFromRoom(userId: String): Result<User> {
        return repository.getCurrentUserFromRoom(userId)
    }
}