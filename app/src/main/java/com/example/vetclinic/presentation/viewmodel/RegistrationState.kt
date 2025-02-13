package com.example.vetclinic.presentation.viewmodel

import com.google.firebase.auth.FirebaseUser

sealed class RegistrationState {

    object Loading : RegistrationState()
    data class Result(val user: FirebaseUser) : RegistrationState()
    data class Error(val message: String?) : RegistrationState()
}