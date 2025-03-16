package com.example.vetclinic.presentation.viewmodel

sealed class ResetPasswordState {

    object Success : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
    object Loading : ResetPasswordState()
}