package com.example.vetclinic.presentation.viewmodel.registration

import com.example.vetclinic.domain.entities.PetInputData
import com.example.vetclinic.domain.entities.UserInputData

sealed class RegistrationState {
    data class Result(
        val userdata: UserInputData? = null,
        val petData: PetInputData? = null
    ) : RegistrationState()

    data object Loading: RegistrationState()
    data object Success: RegistrationState()
    data class Error(val message: String?) : RegistrationState()
}
