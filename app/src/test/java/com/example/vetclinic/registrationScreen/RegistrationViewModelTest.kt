package com.example.vetclinic.registrationScreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vetclinic.domain.entities.pet.PetInputData
import com.example.vetclinic.domain.entities.user.UserInputData
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.RegisterUserUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.domain.usecases.UserUseCase
import com.example.vetclinic.presentation.screens.loginScreen.registrationScreen.RegistrationState
import com.example.vetclinic.presentation.screens.loginScreen.registrationScreen.RegistrationViewModel
import com.example.vetclinic.utils.Validator
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    private val registerUserUseCase: RegisterUserUseCase = mock()
    private val petUseCase: PetUseCase = mock()
    private val userUseCase: UserUseCase = mock()
    private val sessionUseCase: SessionUseCase = mock()
    private val userValidator: Validator<UserInputData> = mock()
    private val petValidator: Validator<PetInputData> = mock()

    private lateinit var viewModel: RegistrationViewModel
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
    fun `updateFormState updates both user and pet data`() {
        val userData = UserInputData(
            "Test", "User", "123",
            "email@example.com", "123456"
        )

        val petData = PetInputData("TestPet", "Cat", "2022-01-07", "Male")

        viewModel.updateFormState(userData, petData)

        val state = viewModel.registrationState.value
        assertTrue(state is RegistrationState.Result)

        val result = state as RegistrationState.Result

        assertEquals(userData, result.userdata)
        assertEquals(petData, result.petData)
    }

    @Test
    fun `registerUser returns error when input fields are empty`() = runTest {
        val userData = UserInputData("", "", "", "", "")
        val petData = PetInputData("", "", "", "")

        viewModel.updateFormState(userData, petData)

        whenever(userValidator.validate(userData)).thenReturn("Данные пользователя " +
                "не должны быть пустыми")
        whenever(petValidator.validate(petData)).thenReturn(null)

        viewModel.registerUser()

        val state = viewModel.registrationState.getValue()
        assertTrue(state is RegistrationState.Error)
        assertEquals(
            "Данные пользователя не должны быть пустыми",
            (state as RegistrationState.Error).message
        )
    }

    @Test
    fun `registerUser returns error when user input data is null`() = runTest {

        viewModel.updateFormState(null, PetInputData("Pet", "Cat",
            "2022-01-01", "Male"))
        viewModel.registerUser()

        val state = viewModel.registrationState.value
        assertTrue(state is RegistrationState.Error)
        assertEquals(
            "Данные пользователя не должны быть пустыми",
            (state as RegistrationState.Error).message
        )
    }


    @Test
    fun `registerUser returns error when petInputData is null`() = runTest {
        viewModel.updateFormState(
            UserInputData("Name", "Last",
                "123", "email@test.com", "79809"),
            null
        )

        viewModel.registerUser()

        val state = viewModel.registrationState.value
        assertTrue(state is RegistrationState.Error)
        assertEquals(
            "Данные питомца не должны быть пустыми",
            (state as RegistrationState.Error).message
        )
    }
}