package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.User

sealed class RegistrationState {
    object Result : RegistrationState()
    data class Error(val message: String?) : RegistrationState()
}