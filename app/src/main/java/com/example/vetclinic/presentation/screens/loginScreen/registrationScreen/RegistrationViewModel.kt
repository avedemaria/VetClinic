package com.example.vetclinic.presentation.screens.loginScreen.registrationScreen

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
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

class RegistrationViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val petUseCase: PetUseCase,
    private val userUseCase: UserUseCase,
    private val sessionUseCase: SessionUseCase,
    private val userValidator: Validator<UserInputData>,
    private val petValidator: Validator<PetInputData>,
) : ViewModel() {


    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

    fun registerUser() {

        val currentState = _registrationState.value as? RegistrationState.Result
        val userInputData = currentState?.userdata
        val petInputData = currentState?.petData

        validateInputs(userInputData, petInputData)?.let {
            _registrationState.value = RegistrationState.Error(it)
            return
        }

        _registrationState.value = RegistrationState.Loading

        viewModelScope.launch()
        {
            userInputData?.let {
                registerUserUseCase.registerUser(it.email, it.password)
            }?.onSuccess { userSession ->
                Timber.d(TAG, "Auth successful, creating user with ID: ${userSession.user?.id}")
                handleSuccessfulRegistration(userSession, userInputData, petInputData!!)
            }
                ?.onFailure { error ->
                    Timber.e(TAG, "Failed to register", error)
                    _registrationState.value = RegistrationState.Error(error.message)
                    Timber.d(TAG, " state set to ${_registrationState.value}")
                }

        }
    }


    private suspend fun handleSuccessfulRegistration(
        userSession: UserSession,
        userInputData: UserInputData,
        petInputData: PetInputData,
    ) {
        val userId = userSession.user?.id ?: return
        val petId = UUID.randomUUID().toString()

        val user = createUserAfterRegistration(userInputData, userId)
        val pet = createPetAfterRegistration(petInputData, userId, petId)

        runCatching {
            saveUserSession(userId, userSession.accessToken, userSession.refreshToken).getOrThrow()
            addAndFetchUserFromSupabaseDb(user).getOrThrow()
            addAndFetchPetFromSupabaseDb(pet, user).getOrThrow()
        }.fold(
            onSuccess = { _registrationState.value = RegistrationState.Success },
            onFailure = { error -> _registrationState.value = RegistrationState.Error(error.message) }
        )
    }


    private suspend fun addAndFetchUserFromSupabaseDb(user: User): Result<Unit> {
        return try {
            userUseCase.addUserToSupabaseDb(user).getOrThrow()
            userUseCase.getUserFromSupabaseDb(user.uid).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to add or fetch user from Supabase")
            Result.failure(e)
        }
    }


    private suspend fun addAndFetchPetFromSupabaseDb(pet: Pet, user: User): Result<Unit> {
        return try {
            petUseCase.addPetToSupabaseDb(pet).getOrThrow()
            petUseCase.getPetsFromSupabaseDb(user.uid).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to add or fetch user from Supabase")
            Result.failure(e)
        }
    }

    private suspend fun saveUserSession(
        userId: String,
        accessToken: String,
        refreshToken: String,
    ): Result<Unit> {
       return try {
           sessionUseCase.saveUserSession(
               userId,
               accessToken,
               refreshToken
           )
           Result.success(Unit)
       } catch (e:Exception) {
           Timber.tag(TAG).e(e, "Failed to save user session to datastore")
           Result.failure(e)
       }
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
        _registrationState.value = RegistrationState.Result(
            userdata = userData,
            petData = petData
        )
    }


    private fun validateInputs(
        userInputData: UserInputData?,
        petInputData: PetInputData?
    ): String? {
        userValidator.validate(userInputData)?.let { return it }
        petValidator.validate(petInputData)?.let { return it }
        return null
    }


    private fun showError(message: String) {
        _registrationState.value = RegistrationState.Error(message)
    }



    companion object {
        private const val TAG = "RegistrationViewModel"
    }
}


