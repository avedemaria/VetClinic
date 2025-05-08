package com.example.vetclinic.presentation.screens.updatePasswordScreen

sealed class UpdatePasswordState {

    data object Success : UpdatePasswordState()
    data class Error(val message: String) : UpdatePasswordState()
    data object Loading : UpdatePasswordState()
}