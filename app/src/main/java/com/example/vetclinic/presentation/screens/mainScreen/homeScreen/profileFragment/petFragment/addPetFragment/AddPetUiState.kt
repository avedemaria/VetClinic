package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment

sealed class AddPetUiState {

    data object Loading : AddPetUiState()
    data object Success : AddPetUiState()
    data object Error : AddPetUiState()
}