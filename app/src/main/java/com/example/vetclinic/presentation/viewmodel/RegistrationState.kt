package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.authFeature.User

sealed class RegistrationState {
    data class Result(val user: User) : RegistrationState()
    data class Error(val message: String?) : RegistrationState()
}