package com.example.vetclinic.presentation.addPetScreen

sealed class AddPetUiState {

    data object Loading : AddPetUiState()
    data object Success : AddPetUiState()
    data class Error(val message: String) : AddPetUiState()
}