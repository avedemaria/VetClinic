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
            .onSuccess { userDto ->
                userDto?.let {
                    val userDbModel = userMapper.userDtoToUserDbModel(it)
                    userLocalDataSource.addUser(userDbModel).getOrThrow()
                }
            }
            .map { userDto ->
                userDto?.let { userMapper.userDtoToUserEntity(it) }
            }
    }

    override suspend fun addUserToSupabaseDb(user: User): Result<Unit> {
        return runCatching {
            val userDto = userMapper.userEntityToUserDto(user)
            userRemoteDataSource.addUser(userDto).getOrThrow()
        }
    }


    override suspend fun updateUserInSupabaseDb(userId: String, updatedUser: User): Result<Unit> {
        return runCatching {
            val userDto = userMapper.userEntityToUserDto(updatedUser)
            userRemoteDataSource.updateUser(userId, userDto).getOrThrow()
                .also {
                    updateUserInRoom(updatedUser)
                }
        }
    }


    override suspend fun updateUserInRoom(user: User): Result<Unit> {
        return runCatching {
            val userDbModel = userMapper.userEntityToUserDbModel(user)
            userLocalDataSource.updateUser(userDbModel).getOrThrow()
        }
    }


    override suspend fun getCurrentUserFromRoom(userId: String): Result<User> {
        return userLocalDataSource.getCurrentUser(userId)
            .mapCatching { userDbModel ->
                userDbModel ?: throw NoSuchElementException("User with ID $userId not found")
            }
            .map { userDbModel ->
                userMapper.userDbModelToUserEntity(userDbModel)
            }
            .onFailure {
                Timber.tag(TAG).e(it, "Error getting user from Room")
            }
    }

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }


}

