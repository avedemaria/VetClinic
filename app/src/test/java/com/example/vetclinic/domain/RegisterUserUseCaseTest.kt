package com.example.vetclinic.domain

import com.example.vetclinic.domain.repository.AuthRepository
import com.example.vetclinic.domain.usecases.RegisterUserUseCase
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class RegisterUserUseCaseTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: RegisterUserUseCase


    @Before
    fun setUp() {
        repository = mockk()
        useCase = RegisterUserUseCase(repository)
    }


    @After
    fun tearDown() {
        clearAllMocks()
    }


    @Test
    fun `WHEN repository returns success THEN usecase returns success`() = runTest {

        val email = "test@example.com"
        val password = "password123"
        val expectedSession = mockUserSession()
        coEvery { repository.registerUser(email, password) } returns Result.success(expectedSession)

        val result = useCase.registerUser(email, password)

        assert(result.isSuccess)
        assertEquals(expectedSession, result.getOrNull())
    }


    @Test
    fun `WHEN repository returns failure THEN usecase returns failure`() = runTest {

        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Registration failed")
        coEvery { repository.registerUser(email, password) } returns Result.failure(exception)

        val result = useCase.registerUser(email, password)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
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

