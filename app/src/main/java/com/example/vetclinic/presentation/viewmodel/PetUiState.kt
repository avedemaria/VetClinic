package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.User

sealed class PetUiState {

    object Loading : PetUiState()
    data class Success(val pets: List<Pet>) : PetUiState()
    data class Error(val message: String) : PetUiState()

}