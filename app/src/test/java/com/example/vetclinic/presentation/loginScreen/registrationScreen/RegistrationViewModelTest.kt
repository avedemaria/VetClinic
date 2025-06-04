package com.example.vetclinic.presentation.loginScreen.registrationScreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.pet.PetInputData
import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.domain.entities.user.UserInputData
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.RegisterUserUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.domain.usecases.UserUseCase
import com.example.vetclinic.presentation.screens.loginScreen.registrationScreen.RegistrationState
import com.example.vetclinic.presentation.screens.loginScreen.registrationScreen.RegistrationViewModel
import com.example.vetclinic.utils.Validator
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    private val registerUserUseCase: RegisterUserUseCase = mockk()
    private val petUseCase: PetUseCase = mockk()
    private val userUseCase: UserUseCase = mockk()
    private val sessionUseCase: SessionUseCase = mockk()
    private val userValidator: Validator<UserInputData> = mockk()
    private val petValidator: Validator<PetInputData> = mockk()

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var mockUserSession: UserSession
    private var mockUser: User = User("", "", "", "", "")
    private var mockPet: Pet = Pet("", "", "", "", "", "")


    private val testDispatcher = StandardTestDispatcher()


    @get:Rule
    val instantRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegistrationViewModel(
            registerUserUseCase,
            petUseCase,
            userUseCase,
            sessionUseCase,
            userValidator,
            petValidator
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `WHEN pet addition to remote db fails THEN registration fails`() = testUseCaseError(
        setup = {
            val userData = createUserData()
            val petData = createPetData()
            setupValidData(
                userData,
                petData,
                addPetResult = Result.failure(Exception("Failure while adding pet to sb"))
            )
            viewModel.updateFormState(userData, petData)
        },
        invoke = { viewModel.registerUser() },
        assertState = {
            val state = viewModel.registrationState.value
            assertTrue(state is RegistrationState.Error)
        },
        verify = {
            coVerify(exactly = 1) {
                petUseCase.addPetToSupabaseDb(any())
            }
        }
    )


    @Test
    fun `WHEN fetching pet from remote db fails after successful pet addition THEN registration fails`() =
        testUseCaseError(
            setup = {
                val userData = createUserData()
                val petData = createPetData()
                setupValidData(
                    userData,
                    petData,
                    getPetsResult = Result.failure(Exception("Failure while getting pet from sb"))
                )
                viewModel.updateFormState(userData, petData)
            },
            invoke = { viewModel.registerUser() },
            assertState = {
                val state = viewModel.registrationState.value
                assertTrue(state is RegistrationState.Error)
            },
            verify = {
                coVerify(exactly = 1) {
                    petUseCase.getPetsFromSupabaseDb(any())
                }
            }
        )


    @Test
    fun `WHEN input is valid THEN registration succeeds`() = runTest(testDispatcher) {
        val userData = createUserData()
        val petData = createPetData()

        setupValidData(userData, petData)

        viewModel.updateFormState(userData, petData)
        viewModel.registerUser()
        advanceUntilIdle()

        val state = viewModel.registrationState.value
        assertTrue("Expected Success but got $state", state is RegistrationState.Success)

        coVerify { registerUserUseCase.registerUser(userData.email, userData.password) }
        coVerify { userUseCase.addUserToSupabaseDb(any()) }
        coVerify { petUseCase.addPetToSupabaseDb(any()) }
        coVerify { sessionUseCase.saveUserSession(any(), "token123", "refresh123") }
    }

    @Test
    fun `WHEN registerUserUseCase fails THEN registration fails`() = testUseCaseError(
        setup = {
            val userData = createUserData()
            val petData = createPetData()
            setupValidData(userData, petData)
            viewModel.updateFormState(userData, petData)
            coEvery {
                registerUserUseCase.registerUser(
                    userData.email,
                    userData.password
                )
            } returns Result.failure(
                Exception("Registration failure")
            )
        },
        invoke = { viewModel.registerUser() },
        assertState = {
            val state = viewModel.registrationState.value
            assertTrue(state is RegistrationState.Error)
        },
        verify = {
            val userData = createUserData()
            coVerify(exactly = 1) {
                registerUserUseCase.registerUser(
                    userData.email,
                    userData.password
                )
            }
        }
    )

    @Test
    fun `WHEN saving session to data store fails THEN registration fails`() = testUseCaseError(
        setup = {
            val userData = createUserData()
            val petData = createPetData()
            setupValidData(
                userData,
                petData,
                shouldSessionSaveFail = true
            )
            viewModel.updateFormState(userData, petData)
        },
        invoke = { viewModel.registerUser() },
        assertState = {
            val state = viewModel.registrationState.value
            assertTrue(state is RegistrationState.Error)
        },
        verify = {
            coVerify(exactly = 1) {
                sessionUseCase.saveUserSession(any(), any(), any())
            }
        }
    )


    @Test
    fun `WHEN fetching user from remote db fails THEN registration fails`() = testUseCaseError(
        setup = {
            val userData = createUserData()
            val petData = createPetData()
            setupValidData(
                userData, petData, getUserResult = Result.failure(
                    Exception("Failure while getting user from supabase")
                )
            )
            viewModel.updateFormState(userData, petData)

        },
        invoke = { viewModel.registerUser() },
        assertState = {
            val state = viewModel.registrationState.value
            println("$state")
            assertTrue(state is RegistrationState.Error)

        },
        verify = {
            coVerify(exactly = 1) { userUseCase.getUserFromSupabaseDb(any()) }
        }
    )

    @Test
    fun `WHEN adding user to remote db fails THEN registration fails`() = testUseCaseError(
        setup = {
            val userData = createUserData()
            val petData = createPetData()
            setupValidData(userData, petData)
            viewModel.updateFormState(userData, petData)
            coEvery { userUseCase.addUserToSupabaseDb(any()) } returns Result.failure(
                Exception("Failure while adding user to supabase")
            )
        },
        invoke = { viewModel.registerUser() },
        assertState = {
            val state = viewModel.registrationState.value
            assertTrue(state is RegistrationState.Error)
        },
        verify = {
            coVerify(exactly = 1) { userUseCase.addUserToSupabaseDb(any()) }
        }
    )


    @Test
    fun `should updateFormState update both user and pet data`() {

        val userData = createUserData()
        val petData = createPetData()

        viewModel.updateFormState(userData, petData)

        val state = viewModel.registrationState.value
        assertTrue(state is RegistrationState.Result)

        val result = state as RegistrationState.Result

        assertEquals(userData, result.userdata)
        assertEquals(petData, result.petData)
    }

    @Test
    fun `WHEN input fields are empty THEN registerUser returns error`() = runTest {
        val userData = createUserData("", "", "", "", "")
        val petData = createPetData("", "", "", "")

        viewModel.updateFormState(userData, petData)

        every { userValidator.validate(userData) } returns "Данные пользователя " + "не должны быть пустыми"

        every { petValidator.validate(petData) } returns null

        viewModel.registerUser()

        val state = viewModel.registrationState.getValue()
        assertTrue(state is RegistrationState.Error)
        assertEquals(
            "Данные пользователя не должны быть пустыми",
            (state as RegistrationState.Error).message
        )
    }

    @Test
    fun `WHEN user input data is null THEN register user return error`() = runTest {

        viewModel.updateFormState(
            null, createPetData()
        )

        every { userValidator.validate(null) } returns "Данные пользователя не должны быть пустыми"
        every { petValidator.validate(createPetData()) } returns null

        viewModel.registerUser()

        val state = viewModel.registrationState.value
        assertTrue(state is RegistrationState.Error)
        assertEquals(
            "Данные пользователя не должны быть пустыми",
            (state as RegistrationState.Error).message
        )
    }


    @Test
    fun `WHEN petInputData is null THEN register user returns error`() = runTest {
        viewModel.updateFormState(
            createUserData(),
            null
        )

        every { userValidator.validate(createUserData()) } returns null
        every { petValidator.validate(null) } returns "Данные питомца не должны быть пустыми"


        viewModel.registerUser()

        val state = viewModel.registrationState.value
        assertTrue(state is RegistrationState.Error)
        assertEquals(
            "Данные питомца не должны быть пустыми",
            (state as RegistrationState.Error).message
        )
    }

    private fun testUseCaseError(
        setup: suspend () -> Unit,
        invoke: suspend () -> Unit,
        assertState: () -> Unit,
        verify: suspend () -> Unit,
    ) = runTest {
        setup()
        invoke()
        advanceUntilIdle()
        assertState()
        verify()
    }


    private fun setupValidData(
        userData: UserInputData,
        petData: PetInputData,
        getUserResult: Result<User> = Result.success(mockUser),
        addUserResult: Result<Unit> = Result.success(Unit),
        addPetResult: Result<Unit> = Result.success(Unit),
        getPetsResult: Result<List<Pet>> = Result.success(listOf(mockPet)),
        shouldSessionSaveFail: Boolean = false,
    ) {
        mockUserSession = UserSession(
            user = UserInfo(id = "2344", aud = "7898"),
            accessToken = "token123",
            refreshToken = "refresh123",
            expiresIn = 1000L,
            tokenType = "testTokenType"
        )

        mockUser = User("123", userData.name, userData.lastName, userData.phone, userData.email)
        mockPet = Pet("1234", "123", petData.name, petData.bDay, petData.type, petData.gender)


        every { userValidator.validate(userData) } returns null
        every { petValidator.validate(petData) } returns null

        coEvery {
            registerUserUseCase.registerUser(userData.email, userData.password)
        } returns Result.success(mockUserSession)

        coEvery { userUseCase.addUserToSupabaseDb(any()) } returns addUserResult
        coEvery { userUseCase.getUserFromSupabaseDb(any()) } returns getUserResult
        coEvery { petUseCase.addPetToSupabaseDb(any()) } returns addPetResult
        coEvery { petUseCase.getPetsFromSupabaseDb(any()) } returns getPetsResult

        if (shouldSessionSaveFail) {
            coEvery {
                sessionUseCase.saveUserSession(
                    any(),
                    any(),
                    any()
                )
            } throws Exception("Failure while saving session")
        } else {
            coEvery { sessionUseCase.saveUserSession(any(), any(), any()) } returns Unit
        }
    }


    private fun createPetData(
        name: String = "Pet",
        type: String = "Cat",
        bDay: String = "04-05-2022",
        gender: String = "Female",
    ) = PetInputData(name, type, bDay, gender)

    private fun createUserData(
        name: String = "Test",
        lastName: String = "Surname",
        phone: String = "8982787",
        email: String = "test@test.ru",
        password: String = "1123",
    ) = UserInputData(name, lastName, phone, email, password)
}