package com.example.vetclinic.data

import com.example.vetclinic.data.localSource.database.models.UserDbModel
import com.example.vetclinic.data.localSource.interfaces.UserLocalDataSource
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.remoteSource.interfaces.UserRemoteDataSource
import com.example.vetclinic.data.remoteSource.network.model.UserDto
import com.example.vetclinic.data.repositoryImpl.UserRepositoryImpl
import com.example.vetclinic.domain.entities.user.User
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var userRemoteDataSource: UserRemoteDataSource
    private lateinit var userLocalDataSource: UserLocalDataSource
    private lateinit var userMapper: UserMapper
    private lateinit var repository: UserRepositoryImpl


    @Before
    fun setUp() {
        userRemoteDataSource = mockk()
        userLocalDataSource = mockk()
        userMapper = mockk()
        repository = UserRepositoryImpl(userRemoteDataSource, userLocalDataSource, userMapper)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }


    @Test
    fun `WHEN getUserFromSupabaseDb succeeds THEN return mapped User and store locally`() = runTest {
        val userId = "123"
        val userDto = mockk<UserDto>()
        val userDbModel = mockk<UserDbModel>()
        val userEntity = mockk<User>()

        coEvery { userRemoteDataSource.getUserById(userId) } returns Result.success(userDto)
        coEvery { userMapper.userDtoToUserDbModel(userDto) } returns userDbModel
        coEvery { userLocalDataSource.addUser(userDbModel) } returns Result.success(Unit)
        coEvery { userMapper.userDtoToUserEntity(userDto) } returns userEntity

        val result = repository.getUserFromSupabaseDb(userId)

        assertTrue(result.isSuccess)
        assertEquals(userEntity, result.getOrNull())

        coVerify { userRemoteDataSource.getUserById(userId) }
        coVerify { userMapper.userDtoToUserDbModel(userDto) }
        coVerify { userMapper.userDtoToUserEntity(userDto) }
    }


    @Test
    fun `WHEN getUserFromSupabaseDb fails THEN return failure`() = runTest {
        val userId = "123"
        val exception = Exception("Remote fetch error")

        coEvery { userRemoteDataSource.getUserById(userId) } returns Result.failure(exception)

        val result = repository.getUserFromSupabaseDb(userId)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) { userRemoteDataSource.getUserById(userId) }
    }

    @Test
    fun `WHEN addUserToSupabaseDb succeeds THEN return success`() = runTest {
        val user = mockk<User>()
        val userDto = mockk<UserDto>()

        coEvery { userMapper.userEntityToUserDto(user) } returns userDto
        coEvery { userRemoteDataSource.addUser(userDto) } returns Result.success(Unit)

        val result = repository.addUserToSupabaseDb(user)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { userMapper.userEntityToUserDto(user) }
        coVerify(exactly = 1) { userRemoteDataSource.addUser(userDto) }
    }

    @Test
    fun `WHEN addUserToSupabaseDb fails THEN return failure`() = runTest {
        val user = mockk<User>()
        val userDto = mockk<UserDto>()
        val exception = Exception("Adding failed")

        coEvery { userMapper.userEntityToUserDto(user) } returns userDto
        coEvery { userRemoteDataSource.addUser(userDto) } returns Result.failure(exception)

        val result = repository.addUserToSupabaseDb(user)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { userMapper.userEntityToUserDto(user) }
        coVerify(exactly = 1) { userRemoteDataSource.addUser(userDto) }
    }

    @Test
    fun `WHEN updateUserInSupabaseDb succeeds THEN update Room and return success`() = runTest {
        val userId = "123"
        val user = mockk<User>()
        val userDto = mockk<UserDto>()
        val userDbModel = mockk<UserDbModel>()

        coEvery { userMapper.userEntityToUserDto(user) } returns userDto
        coEvery { userRemoteDataSource.updateUser(userId, userDto) } returns Result.success(Unit)
        coEvery { userMapper.userEntityToUserDbModel(user) } returns userDbModel
        coEvery { userLocalDataSource.updateUser(userDbModel) } returns Result.success(Unit)

        val result = repository.updateUserInSupabaseDb(userId, user)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { userRemoteDataSource.updateUser(userId, userDto) }
        coVerify(exactly = 1) { userMapper.userEntityToUserDbModel(user) }
        coVerify(exactly = 1) { userLocalDataSource.updateUser(userDbModel) }
    }

    @Test
    fun `WHEN updateUserInSupabaseDb fails THEN return failure`() = runTest {
        val userId = "123"
        val user = mockk<User>()
        val userDto = mockk<UserDto>()
        val exception = Exception("Remote update failed")

        coEvery { userMapper.userEntityToUserDto(user) } returns userDto
        coEvery { userRemoteDataSource.updateUser(userId, userDto) } returns Result.failure(exception)

        val result = repository.updateUserInSupabaseDb(userId, user)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) { userRemoteDataSource.updateUser(userId, userDto) }

    }


    @Test
    fun `WHEN getCurrentUserFromRoom succeeds THEN return mapped User`() = runTest {
        val userId = "123"
        val dbModel = mockk<UserDbModel>()
        val user = mockk<User>()

        coEvery { userLocalDataSource.getCurrentUser(userId) } returns Result.success(dbModel)
        coEvery { userMapper.userDbModelToUserEntity(dbModel) } returns user

        val result = repository.getCurrentUserFromRoom(userId)

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())

        coVerify(exactly = 1) { userMapper.userDbModelToUserEntity(dbModel) }
        coVerify(exactly = 1) { userLocalDataSource.getCurrentUser(userId) }
    }

    @Test
    fun `WHEN getCurrentUserFromRoom returns null THEN return failure`() = runTest {
        val userId = "123"

        coEvery { userLocalDataSource.getCurrentUser(userId) } returns Result.success(null)

        val result = repository.getCurrentUserFromRoom(userId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoSuchElementException)

        confirmVerified(userMapper)
        coVerify(exactly = 1) { userLocalDataSource.getCurrentUser(userId) }
    }
}