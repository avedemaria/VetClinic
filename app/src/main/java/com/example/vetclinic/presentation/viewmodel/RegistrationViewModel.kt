package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.User
import com.example.vetclinic.domain.usecases.AddUserToSupabaseDb
import com.example.vetclinic.domain.usecases.RegisterUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel @Inject constructor(
    private val addUserToSupabaseDb: AddUserToSupabaseDb,
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {


    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> get() = _registrationState


    fun registerUser(
        userName: String,
        userLastName: String,
        petName: String,
        phoneNumber: String,
        email: String,
        password: String
    ) {

        if (!validateInputs(userName, userLastName, petName, phoneNumber, email, password)) {
            return
        }


//        _registrationState.value = RegistrationState.Loading


        try {
            Log.d("RegistrationVM", "Starting registration process")
            viewModelScope.launch(Dispatchers.IO) {
                registerUserUseCase.registerUser(email, password)
                    .onSuccess { supabaseUser ->
                        Log.d(
                            "RegistrationVM",
                            "Auth successful, creating user with ID: ${supabaseUser.user?.id}"
                        )
                        val user = User(
                            supabaseUser.user?.id ?: "",
                            userName,
                            userLastName,
                            petName,
                            phoneNumber,
                            email
                        )


                        addUserToSupabaseDb.addUserToSupabaseDb(user).onSuccess {
                            Log.d("RegistrationVM", "user added to supabase $user")
                            _registrationState.postValue(RegistrationState.Result(user))
                        }.onFailure { error ->
                            Log.e("RegistrationVM", "Failed to add user to DB", error)
                            _registrationState.postValue(
                                RegistrationState.Error(
                                    error.message ?: "Failed to create user profile"
                                )
                            )
                        }

                    }
                    .onFailure { error ->
                        Log.e("RegistrationVM", "Failed to add user to DB", error)
                        _registrationState.postValue(RegistrationState.Error(error.message))
                    }
            }
        } catch (e: Exception) {
            Log.e("RegistrationVM", "unexpected error", e)
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
        when {
            name.isBlank() -> {
                _registrationState.value = RegistrationState.Error("Name is required")
                return false
            }

            lastName.isBlank() -> {
                _registrationState.value = RegistrationState.Error("Last name is required")
                return false
            }

            petName.isBlank() -> {
                _registrationState.value = RegistrationState.Error("Pet name is required")
                return false
            }

            phoneNumber.isBlank() -> {
                _registrationState.value = RegistrationState.Error("Phone number is required")
                return false
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _registrationState.value = RegistrationState.Error("Valid email is required")
                return false
            }

            password.length < 6 -> {
                _registrationState.value =
                    RegistrationState.Error("Password must be at least 6 characters")
                return false
            }
        }
        return true
    }

}
