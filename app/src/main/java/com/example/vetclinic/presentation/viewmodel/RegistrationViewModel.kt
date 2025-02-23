package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.User
import com.example.vetclinic.domain.usecases.AddUserToSupabaseDb
import com.example.vetclinic.domain.authFeature.RegisterUserUseCase
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.usecases.AddUserToRoomUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class RegistrationViewModel @Inject constructor(
    private val addUserToSupabaseDb: AddUserToSupabaseDb,
    private val registerUserUseCase: RegisterUserUseCase,
    private val addUserToRoomUseCase: AddUserToRoomUseCase
) : ViewModel() {


    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> get() = _registrationState


    fun registerUser(
        userName: String,
        userLastName: String,
        petName: String,
        phoneNumber: String,
        email: String,
        password: String,
    ) {

        if (!validateInputs(userName, userLastName, petName, phoneNumber, email, password)) {
            return
        }

        try {
            Log.d(TAG, "Starting registration process")
            viewModelScope.launch(Dispatchers.IO) {
                registerUserUseCase.registerUser(email, password)
                    .onSuccess { supabaseUser ->
                        Log.d(
                            TAG,
                            "Auth successful, creating user with ID: ${supabaseUser.user?.id}"
                        )

                        val userId = supabaseUser.user?.id ?: return@onSuccess
                        val petId = UUID.randomUUID().toString()

                        val user = User(
                            userId,
                            userName,
                            userLastName,
                            phoneNumber,
                            email
                        )

                        val pet = Pet(
                            petId,
                            userId,
                            petName
                        )


                        addUserToSupabaseDb.addUserToSupabaseDb(user).onSuccess {
                            Log.d(TAG, "user added to supabase $user")

                            addUserToSupabaseDb.addPetToSupabaseDb(pet).onSuccess {
                                Log.d(TAG, "pet added to supabase $pet")

                                addUserToRoomUseCase.addUserToRoom(user, pet)

                                Log.d(TAG, "User and pet added to Room")
                                _registrationState.postValue(RegistrationState.Result(user))
                            }


                        }.onFailure { error ->
                            Log.e(TAG, "Failed to add user to DB", error)
                            _registrationState.postValue(
                                RegistrationState.Error(
                                    error.message ?: "Failed to add user to DB"
                                )
                            )
                        }

                    }
                    .onFailure { error ->
                        Log.e(TAG, "Failed to register", error)
                        _registrationState.postValue(RegistrationState.Error(error.message))
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "unexpected error", e)
            _registrationState.postValue(RegistrationState.Error("Unexpected error ${e.message}"))
        }

    }





    private fun validateInputs(
        name: String,
        lastName: String,
        petName: String,
        phoneNumber: String,
        email: String,
        password: String
    ): Boolean {
        val validations = listOf(
            name.isNotBlank() to "Name is required",
            lastName.isNotBlank() to "Last name is required",
            petName.isNotBlank() to "Pet name is required",
            phoneNumber.isNotBlank() to "Phone number is required",
            android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() to "Valid email is required",
            (password.length >= 6) to "Password must be at least 6 characters"
        )

        validations.firstOrNull { !it.first }?.let { (_, errorMessage) ->
            _registrationState.value = RegistrationState.Error(errorMessage)
            return false
        }

        return true
    }


    companion object {
        const val TAG = "RegistrationVM"
    }
}
