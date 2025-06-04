package com.example.vetclinic.data.repositoryImpl

import com.example.vetclinic.data.localSource.interfaces.UserLocalDataSource
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.remoteSource.interfaces.UserRemoteDataSource
import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.domain.repository.UserRepository
import jakarta.inject.Inject
import timber.log.Timber

class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val userMapper: UserMapper,
) : UserRepository {


    override suspend fun getUserFromSupabaseDb(userId: String): Result<User?> {
        return userRemoteDataSource.getUserById(userId)
            .mapCatching { userDto ->
                userDto?.let {
                    val userDbModel = userMapper.userDtoToUserDbModel(it)
                    userLocalDataSource.addUser(userDbModel).getOrThrow()
                    userMapper.userDtoToUserEntity(it)
                }
            }
            .onFailure {
                Timber.tag(TAG).e(it, "Failed to get user from Supabase")
            }
    }


    override suspend fun addUserToSupabaseDb(user: User): Result<Unit> {
        return runCatching {
            val userDto = userMapper.userEntityToUserDto(user)
            userRemoteDataSource.addUser(userDto).getOrThrow()
        }.onFailure {
            Timber.tag(TAG).e(it, "Failed to add user to Supabase")
        }
    }


    override suspend fun updateUserInSupabaseDb(userId: String, updatedUser: User): Result<Unit> {
        return userMapper.userEntityToUserDto(updatedUser).let {
            userRemoteDataSource.updateUser(userId, it)
        }
            .mapCatching {
                updateUserInRoom(updatedUser).getOrThrow()
            }
            .onFailure {
                Timber.tag(TAG).e(it, "Update failed: ${it.message}")
            }
    }


    override suspend fun updateUserInRoom(user: User): Result<Unit> {
        return runCatching {
            val userDbModel = userMapper.userEntityToUserDbModel(user)
            userLocalDataSource.updateUser(userDbModel).getOrThrow()
        }.onFailure {
            Timber.tag(TAG).e(it, "Room update failed: ${it.message}")

        }
    }


    override suspend fun getCurrentUserFromRoom(userId: String): Result<User> {
        return userLocalDataSource.getCurrentUser(userId)
            .mapCatching { userDbModel ->
                userDbModel ?: throw NoSuchElementException("User with ID $userId not found")
            }
            .map { userMapper.userDbModelToUserEntity(it) }
            .onFailure {
                Timber.tag(TAG).e(it, "Error getting user from Room")
            }
    }

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }


}

