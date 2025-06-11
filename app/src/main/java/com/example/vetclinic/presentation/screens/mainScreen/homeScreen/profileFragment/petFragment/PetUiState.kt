package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment

import com.example.vetclinic.domain.entities.pet.Pet

sealed class PetUiState {

    data object Loading : PetUiState()
    data class Success(val pets: List<Pet>) : PetUiState()
    data object Error : PetUiState()

}