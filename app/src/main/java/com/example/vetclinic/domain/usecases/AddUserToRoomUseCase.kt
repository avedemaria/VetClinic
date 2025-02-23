package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.User
import jakarta.inject.Inject

class AddUserToRoomUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun addUserToRoom(user: User, pet: Pet) {
        repository.addUserToRoom(user, pet)
    }
}