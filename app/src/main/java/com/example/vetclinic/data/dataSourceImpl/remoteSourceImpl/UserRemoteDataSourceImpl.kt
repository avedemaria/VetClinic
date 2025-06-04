package com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl

import com.example.vetclinic.data.DataSourceUtils
import com.example.vetclinic.data.remoteSource.interfaces.UserRemoteDataSource
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.data.remoteSource.network.model.UserDto
import jakarta.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor
    (private val supabaseApiService: SupabaseApiService) :
    UserRemoteDataSource {


    override suspend fun getUserById(userId: String): Result<UserDto?> {
        return DataSourceUtils.executeApiCall {
            supabaseApiService.getUserFromSupabaseDbById("eq.$userId")
        }.mapCatching { userList ->
            userList.find { it.uid == userId }
        }
    }

    override suspend fun addUser(user: UserDto): Result<Unit> {
        return DataSourceUtils.executeUnitApiCall {
            supabaseApiService.addUser(user)
        }
    }

    override suspend fun updateUser(userId: String, updatedUser: UserDto): Result<Unit> {
        return DataSourceUtils.executeUnitApiCall {
            supabaseApiService.updateUser("eq.$userId", updatedUser)
        }
    }
}