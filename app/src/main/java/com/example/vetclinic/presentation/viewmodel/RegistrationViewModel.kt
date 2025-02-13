package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.User
import com.example.vetclinic.domain.usecases.AddUserToFirebaseDb
import com.example.vetclinic.domain.usecases.RegisterUserUseCase
import com.google.firebase.auth.AuthResult
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class RegistrationViewModel @Inject constructor(
    private val addUserToFirebaseDb: AddUserToFirebaseDb,
    private val registerUserUseCase: RegisterUserUseCase// Репозиторий для регистрации
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
        _registrationState.value = RegistrationState.Loading

        viewModelScope.launch {
            registerUserUseCase.registerUser(email, password)
                .onSuccess { firebaseUser ->
                    val user = User(
                        firebaseUser.uid,
                        userName,
                        userLastName,
                        petName,
                        phoneNumber,
                        email,
                    )
                    addUserToFirebaseDb.addUserToFirebaseDb(firebaseUser)
                    _registrationState.value = RegistrationState.Result(firebaseUser)
                }
                .onFailure { error ->
                    _registrationState.value = RegistrationState.Error(error.message)

                }
        }
    }
}
