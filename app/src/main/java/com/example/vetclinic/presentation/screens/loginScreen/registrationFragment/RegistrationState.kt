package com.example.vetclinic.presentation.screens.loginScreen.registrationFragment

import com.example.vetclinic.domain.entities.pet.PetInputData
import com.example.vetclinic.domain.entities.user.UserInputData

sealed class RegistrationState {
    data class Result(
        val userdata: UserInputData? = null,
        val petData: PetInputData? = null
    ) : RegistrationState()

    data object Loading: RegistrationState()
    data object Success: RegistrationState()
    data class Error(val message: String?) : RegistrationState()
}
