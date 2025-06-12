package com.example.vetclinic.presentation.screens.updatePasswordScreen

sealed class UpdatePasswordState {

    data object Success : UpdatePasswordState()
    data object Error : UpdatePasswordState()
    data object Loading : UpdatePasswordState()
}