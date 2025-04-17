package com.example.vetclinic.presentation.profileScreen.petScreen

import com.example.vetclinic.domain.entities.pet.Pet

sealed class PetUiState {

    data object Loading : PetUiState()
    data class Success(val pets: List<Pet>) : PetUiState()
    data class Error(val message: String) : PetUiState()

}