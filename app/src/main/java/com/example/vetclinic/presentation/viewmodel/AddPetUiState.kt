package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.Pet

sealed class AddPetUiState {

    object Loading : AddPetUiState()
    object Success : AddPetUiState()
    data class Error(val message: String) : AddPetUiState()
}