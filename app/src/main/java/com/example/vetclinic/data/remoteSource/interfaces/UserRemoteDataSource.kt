package com.example.vetclinic.data.remoteSource.interfaces

import com.example.vetclinic.data.remoteSource.network.model.UserDto

interface UserRemoteDataSource {

suspend fun getUserById (userId:String): Result<UserDto?>

suspend fun addUser (user: UserDto): Result<Unit>

suspend fun updateUser (userId: String, updatedUser: UserDto): Result<Unit>
}