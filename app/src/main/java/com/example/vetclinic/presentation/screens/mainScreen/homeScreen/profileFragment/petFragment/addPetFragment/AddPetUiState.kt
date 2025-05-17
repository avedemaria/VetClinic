package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment

sealed class AddPetUiState {

    data object Loading : AddPetUiState()
    data object Success : AddPetUiState()
    data class Error(val message: String) : AddPetUiState()
}