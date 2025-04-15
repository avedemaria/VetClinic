package com.example.vetclinic.presentation.viewmodel.registration

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.entities.User
import com.example.vetclinic.domain.usecases.AddUserUseCase
import com.example.vetclinic.domain.authFeature.RegisterUserUseCase
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.PetInputData
import com.example.vetclinic.domain.entities.UserInputData
import com.example.vetclinic.domain.usecases.AddPetUseCase
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.UUID

class RegistrationViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val addPetUseCase: AddPetUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getPetsUseCase: GetPetsUseCase,
    private val userDataStore: UserDataStore,
) : ViewModel() {


    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

//    private val _registrationState = MutableStateFlow(RegistrationState.Result())
//    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()
//
//    private val _registrationEvent = MutableSharedFlow<RegistrationEvent>()
//    val registrationEvent: SharedFlow<RegistrationEvent> = _registrationEvent.asSharedFlow()


    fun registerUser() {

        val currentState = _registrationState.value as? RegistrationState.Result
        val userInputData = currentState?.userdata
        val petInputData = currentState?.petData

        val error = validateInputs(userInputData, petInputData)
        if (error != null) {
            _registrationState.value = RegistrationState.Error(error)
            return
        }

        val userData = userInputData
        val petData = petInputData


        viewModelScope.launch(Dispatchers.IO) {
            registerUserUseCase.registerUser(userData!!.email, userData.password)
                .onSuccess { supabaseUser ->
                    Log.d(
                        TAG,
                        "Auth successful, creating user with ID: ${supabaseUser.user?.id}"
                    )

                    val userId = supabaseUser.user?.id ?: return@onSuccess
                    val petId = UUID.randomUUID().toString()

                    val user = User(
                        userId,
                        userData.name,
                        userData.lastName,
                        userData.phone,
                        userData.email
                    )

                    val pet = Pet(
                        petId,
                        userId,
                        petData!!.name,
                        petData.bDay,
                        petData.type,
                        petData.gender,
                        petAge = calculatePetAge(petData.bDay)
                    )

                    userDataStore.saveUserSession(userId, supabaseUser.accessToken)

                    addUserUseCase.addUserToSupabaseDb(user).onSuccess {
                        Log.d(TAG, "user added to supabase $user")
                        getUserUseCase.getUserFromSupabaseDb(user.uid)

                    }.onFailure { error ->
                        Log.e(TAG, "Failed to add user to DB", error)
                        _registrationState.postValue(
                            RegistrationState.Error(
                                error.message ?: "Failed to add user to DB"
                            )
                        )
                    }


                    addPetUseCase.addPetToSupabaseDb(pet).onSuccess { savedPet ->
                        Log.d(TAG, "pet added to supabase $savedPet")
                        //sync
                        getPetsUseCase.getPetsFromSupabaseDb(userId)

                        _registrationState.postValue(
                            RegistrationState.Success
                        )
                    }

                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to register", error)
                    _registrationState.postValue(RegistrationState.Error(error.message))
                }
        }

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

    private fun validateInputs(user: UserInputData?, pet: PetInputData?): String? {
        Log.d(TAG, "User data: $user, Pet data: $pet")
        if (user == null || pet == null || user.name.isBlank() || user.lastName.isBlank() ||
            user.phone.isBlank() || user.email.isBlank() || user.password.isBlank() ||
            pet.name.isBlank() || pet.type.isBlank() || pet.gender.isBlank() || pet.bDay.isBlank()
        ) {
            return "Все поля должны быть заполнены"
        }


        val phonePattern = "^(?:\\+7|7|8)(\\d{10})$".toRegex()

        return when {
            !phonePattern.matches(user.phone) -> "Введите корректный номер телефона"

            !Patterns.EMAIL_ADDRESS.matcher(user.email).matches() -> "Введите корректный email"

            user.password.length < 6 -> "Пароль должен быть не менее 6 символов"
            else -> null
        }
    }


    private fun calculatePetAge(petBday: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val birthDate = LocalDate.parse(petBday, formatter)
            val currentDate = LocalDate.now()
            val period = Period.between(birthDate, currentDate)

            val year = period.years
            val month = period.months


            val yearText = if (year > 0) {
                "$year ${getYearSuffix(year)}"
            } else {
                ""
            }

            val monthText = if (month > 0 || year == 0) {
                "$month ${getMonthSuffix(month)}"
            } else {
                ""
            }

            listOf(yearText, monthText).filter {
                it.isNotEmpty()
            }.joinToString(" ")

        } catch (e: Exception) {
            Log.e("PetViewModel", "Error calculating pet age: ${e.message}")
            "0 мес."
        }
    }


    private fun getYearSuffix(years: Int): String {
        return when {
            years % 10 == 1 && years % 100 != 11 -> "год"
            years % 10 in 2..4 && (years % 100 !in 12..14) -> "года"
            else -> "лет"
        }
    }

    private fun getMonthSuffix(month: Int): String {
        return "мес."
    }


    companion object {
        private const val TAG = "RegistrationVM"
    }
}
