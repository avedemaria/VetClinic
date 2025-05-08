package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment

sealed class AddPetUiState {

    data object Loading : com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment.AddPetUiState()
    data object Success : com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment.AddPetUiState()
    data class Error(val message: String) : com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment.AddPetUiState()
}