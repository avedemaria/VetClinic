package com.example.vetclinic.data

import com.example.vetclinic.data.remoteSource.interfaces.AuthRemoteSource
import com.example.vetclinic.data.repositoryImpl.AuthRepositoryImpl
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var remoteSource: AuthRemoteSource
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        remoteSource = mockk()
        repository = AuthRepositoryImpl(remoteSource)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `WHEN remoteSource loginUser succeeds THEN return UserSession`() = runTest {

        val email = "test@example.com"
        val password = "password"
        val expectedSession = mockUserSession()
        coEvery { remoteSource.loginUser(email, password) } returns Result.success(
            expectedSession
        )

        val result = repository.loginUser(email, password)

        assertTrue(result.isSuccess)
        assertEquals(expectedSession, result.getOrNull())

        coVerify(exactly = 1) { remoteSource.loginUser(email, password) }
    }


    @Test
    fun `WHEN remoteSource loginUser fails THEN return failure`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val exception = Exception("Login error")

        coEvery { remoteSource.loginUser(email, password) } returns Result.failure(exception)

        val result = repository.loginUser(email, password)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { remoteSource.loginUser(email, password) }
    }



    @Test
    fun `WHEN remoteSource registerUser succeeds THEN return UserSession`() = runTest {

        val email = "test@example.com"
        val password = "password"
        val expectedSession = mockUserSession()
        coEvery { remoteSource.registerUser(email, password) } returns Result.success(
            expectedSession
        )

        val result = repository.registerUser(email, password)

        assertTrue(result.isSuccess)
        assertEquals(expectedSession, result.getOrNull())

        coVerify(exactly = 1) { remoteSource.registerUser(email, password) }
    }

    @Test
    fun `WHEN remoteSource registerUser fails THEN return failure`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val exception = Exception("Registration error")

        coEvery { remoteSource.registerUser(email, password) } returns Result.failure(exception)

        val result = repository.registerUser(email, password)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { remoteSource.registerUser(email, password) }
    }


    @Test
    fun `WHEN remoteSource logOut succeeds THEN return success`() = runTest {

        coEvery { remoteSource.logOut() } returns Result.success(Unit)

        val result = repository.logOut()

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { remoteSource.logOut() }
    }

    @Test
    fun `WHEN remoteSource logOut fails THEN return failure`() = runTest {
        val exception = Exception("Logout error")
        coEvery { remoteSource.logOut() } returns Result.failure(exception)

        val result = repository.logOut()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { remoteSource.logOut() }
    }


    @Test
    fun `WHEN remoteSource resetPasswordWithEmail succeeds THEN return success`() = runTest {

        val email = "test@example.com"
        coEvery { remoteSource.resetPasswordWithEmail(email) } returns Result.success(Unit)

        val result = repository.resetPasswordWithEmail(email)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { remoteSource.resetPasswordWithEmail(email) }
    }


    @Test
    fun `WHEN remoteSource resetPasswordWithEmail fails THEN return failure`() = runTest {
        val email = "test@example.com"
        val exception = Exception("Reset password error")
        coEvery { remoteSource.resetPasswordWithEmail(email) } returns Result.failure(exception)

        val result = repository.resetPasswordWithEmail(email)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { remoteSource.resetPasswordWithEmail(email) }
    }

    @Test
    fun `WHEN remoteSource updatePassword succeeds THEN return success`() = runTest {
        val newPassword = "newPass123"
        val token = "token123"
        val refreshToken = "refreshToken123"
        coEvery {
            remoteSource.updatePassword(
                newPassword,
                token,
                refreshToken
            )
        } returns Result.success(Unit)

        val result = repository.updatePassword(newPassword, token, refreshToken)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { remoteSource.updatePassword(newPassword, token, refreshToken) }
    }


    @Test
    fun `WHEN remoteSource updatePassword fails THEN return failure`() = runTest {
        val newPassword = "newPass123"
        val token = "token123"
        val refreshToken = "refreshToken123"
        val exception = Exception("Update password error")
        coEvery {
            remoteSource.updatePassword(
                newPassword,
                token,
                refreshToken
            )
        } returns Result.failure(exception)

        val result = repository.updatePassword(newPassword, token, refreshToken)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { remoteSource.updatePassword(newPassword, token, refreshToken) }
    }


    @Test
    fun `WHEN remoteSource deleteUserAccount succeeds THEN return success`() = runTest {

        coEvery { remoteSource.deleteUserAccount() } returns Result.success(Unit)

        val result = repository.deleteUserAccount()

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { remoteSource.deleteUserAccount() }
    }


    @Test
    fun `WHEN remoteSource deleteUserAccount fails THEN return failure`() = runTest {
        val exception = Exception("Delete account error")
        coEvery { remoteSource.deleteUserAccount() } returns Result.failure(exception)

        val result = repository.deleteUserAccount()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { remoteSource.deleteUserAccount() }
    }

    private fun mockUserSession(): UserSession {
        return UserSession(
            user = UserInfo(id = "2344", aud = "7898"),
            accessToken = "token123",
            refreshToken = "refresh123",
            expiresIn = 1000L,
            tokenType = "testTokenType"
        )
    }
}