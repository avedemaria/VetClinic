package com.example.vetclinic.presentation.updatePasswordScreen

sealed class UpdatePasswordState {

    data object Success : UpdatePasswordState()
    data class Error(val message: String) : UpdatePasswordState()
    data object Loading : UpdatePasswordState()
}