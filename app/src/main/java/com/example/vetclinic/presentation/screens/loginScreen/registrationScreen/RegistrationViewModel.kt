package com.example.vetclinic.presentation.screens.loginScreen.registrationScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.pet.PetInputData
import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.domain.entities.user.UserInputData
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.RegisterUserUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.domain.usecases.UserUseCase
import com.example.vetclinic.utils.Validator
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class RegistrationViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val petUseCase: PetUseCase,
    private val userUseCase: UserUseCase,
    private val sessionUseCase: SessionUseCase,
    private val userValidator: Validator<UserInputData>,
    private val petValidator: Validator<PetInputData>
) : ViewModel() {


    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

    fun registerUser() {

        val currentState = _registrationState.value as? RegistrationState.Result
        val userInputData = currentState?.userdata
        val petInputData = currentState?.petData

        val validationError = validateInputs(userInputData, petInputData)
        if (validationError != null) {
            _registrationState.value = RegistrationState.Error(validationError)
            return
        }

        viewModelScope.launch(Dispatchers.IO)
        {

            userInputData?.let {
                registerUserUseCase.registerUser(it.email, it.password)
            }?.onSuccess { userSession ->
                Log.d(
                    TAG,
                    "Auth successful, creating user with ID: ${userSession.user?.id}"
                )
                val userId = userSession.user?.id ?: return@onSuccess
                val petId = UUID.randomUUID().toString()

                val user = createUserAfterRegistration(userInputData, userId)
                val pet = createPetAfterRegistration(petInputData!!, userId, petId)

                saveUserSession(userId, userSession.accessToken, userSession.refreshToken)

                addAndFetchUserFromSupabaseDb(user)
                addAndFetchPetFromSupabaseDb(pet, userId)

                _registrationState.postValue(RegistrationState.Success)

            }
                ?.onFailure { error ->
                    Log.e(TAG, "Failed to register", error)
                    _registrationState.postValue(RegistrationState.Error(error.message))
                }

        }
    }


    private suspend fun addAndFetchUserFromSupabaseDb(user: User) {
        userUseCase.addUserToSupabaseDb(user)
            .onSuccess {
                userUseCase.getUserFromSupabaseDb(user.uid)
            }
            .onFailure { error ->
                Log.e(TAG, "Failed to add user to DB", error)
            }
    }

    private suspend fun addAndFetchPetFromSupabaseDb(pet: Pet, userId: String) {
        petUseCase.addPetToSupabaseDb(pet)
            .onSuccess {
                petUseCase.getPetsFromSupabaseDb(userId)
            }
            .onFailure { error ->
                Log.e(TAG, "Failed to add pet to DB", error)
            }
    }


    private suspend fun saveUserSession(userId: String, accessToken: String, refreshToken: String) {
        sessionUseCase.saveUserSession(
            userId,
            accessToken,
            refreshToken
        )
    }

    private fun createUserAfterRegistration(
        userInputData: UserInputData,
        userId: String,
    ): User {
        return User(
            userId,
            userInputData.name,
            userInputData.lastName,
            userInputData.phone,
            userInputData.email
        )
    }


    private fun createPetAfterRegistration(
        petInputData: PetInputData,
        userId: String,
        petId: String,
    ): Pet {
        return Pet(
            petId,
            userId,
            petInputData.name,
            petInputData.bDay,
            petInputData.type,
            petInputData.gender
        )
    }


    fun updateFormState(userData: UserInputData?, petData: PetInputData?) {
        val currentState = _registrationState.value as? RegistrationState.Result

        val updatedUser = userData ?: currentState?.userdata
        val updatedPet = petData ?: currentState?.petData

        _registrationState.value = RegistrationState.Result(
            userdata = updatedUser,
            petData = updatedPet
        )
    }


    private fun validateInputs(
        userInputData: UserInputData?,
        petInputData: PetInputData?,
    ): String? {
        userInputData ?: return "Данные пользователя не должны быть пустыми"
        petInputData ?: return "Данные питомца не должны быть пустыми"

        val userError = userValidator.validate(userInputData)
        if (userError != null) return userError

        val petError = petValidator.validate(petInputData)
        if (petError != null) return petError

        return null
    }

    companion object {
        private const val TAG = "RegistrationViewModel"
    }
}


