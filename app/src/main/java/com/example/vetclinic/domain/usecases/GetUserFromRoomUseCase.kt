package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.User
import jakarta.inject.Inject

class GetUserFromRoomUseCase @Inject constructor(private val repository: Repository) {


      suspend fun getUserFromRoom (userId: String): Result<User> {
       return repository.getCurrentUserFromRoom(userId)
    }
}