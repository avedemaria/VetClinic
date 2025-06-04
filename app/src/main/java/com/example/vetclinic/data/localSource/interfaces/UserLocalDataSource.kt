package com.example.vetclinic.data.localSource.interfaces

import com.example.vetclinic.data.localSource.database.models.UserDbModel

interface UserLocalDataSource {

    suspend fun updateUser(user: UserDbModel): Result<Unit>

    suspend fun getCurrentUser(userId: String): Result<UserDbModel?>

    suspend fun addUser (user: UserDbModel) : Result <Unit>
}