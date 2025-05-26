package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.domain.repository.UserRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val userMapper: UserMapper,
    private val vetClinicDao: VetClinicDao,

    ) : UserRepository {

    override suspend fun getUserFromSupabaseDb(userId: String): Result<User?> = runCatching {

        val idWithParameter = "eq.$userId"
        val response = supabaseApiService.getUserFromSupabaseDbById(idWithParameter)

        if (!response.isSuccessful) {
            throw Exception("Server error: ${response.code()} - ${response.errorBody()?.string()}")
        }

        val userDtos = response.body() ?: emptyList()
        val userDto = userDtos.find { it.uid == userId }

        if (userDto != null) {
            val user = userMapper.userDtoToUserDbModel(userDto)
            vetClinicDao.insertUser(user)
        }

        return@runCatching userDto?.let { userMapper.userDtoToUserEntity(it) }
    }.onFailure { e ->
        Log.e(TAG, "Error fetching User: ${e.message}", e)
    }


    override suspend fun addUserToSupabaseDb(user: User): Result<Unit> =
        RepositoryUtils.addDataToSupabaseDb(
            entity = user,
            apiCall = { userDto -> supabaseApiService.addUser(userDto) },
            mapper = { userMapper.userEntityToUserDto(user) }
        )

    override suspend fun updateUserInSupabaseDb(userId: String, updatedUser: User): Result<Unit> =
        kotlin.runCatching {

            val userIdWithParam = "eq.$userId"
            val updatedUserDto = userMapper.userEntityToUserDto(updatedUser)
            val response = supabaseApiService.updateUser(userIdWithParam, updatedUserDto)

            if (response.isSuccessful) {
                Log.d(TAG, "Successfully updated user in Supabase DB")
                updateUserInRoom(updatedUser)
                Unit
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to update User. Error: $errorBody")
                throw Exception("Failed to update User. ${response.code()} - $errorBody")
            }
        }
            .onFailure { error ->
                Log.e(TAG, "Error while updating User in Supabase DB", error)
            }


    override suspend fun updateUserInRoom(user: User): Result<Unit> = kotlin.runCatching {

        val userDbModel = userMapper.userEntityToUserDbModel(user)
        vetClinicDao.updateUser(userDbModel)
        Log.d(TAG, "User updated successfully in Room")
        Unit
    }
        .onFailure { error ->
            Log.e(TAG, "Error updating user in Room", error)
        }


    override suspend fun getCurrentUserFromRoom(userId: String): Result<User> =
        runCatching {
            withContext(Dispatchers.IO) {
                val userDbModel = vetClinicDao.getUserById(userId)
                    ?: throw NoSuchElementException("User with ID $userId not found in Room")
                userMapper.userDbModelToUserEntity(userDbModel)
            }
        }.onFailure {
            Log.e(TAG, "Error while getting user from Room", it)
        }


    companion object {
        private const val TAG = "UserRepositoryImpl"
    }
}

