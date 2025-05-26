package com.example.vetclinic.domain.repository

import com.example.vetclinic.domain.entities.user.User

interface UserRepository {

    suspend fun getUserFromSupabaseDb(userId: String): Result<User?>

    suspend fun updateUserInSupabaseDb(userId: String, updatedUser: User): Result<Unit>

    suspend fun addUserToSupabaseDb(user: User): Result<Unit>

    suspend fun updateUserInRoom(user: User): Result<Unit>

    suspend fun getCurrentUserFromRoom(userId: String): Result<User>

}