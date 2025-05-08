package com.example.vetclinic.presentation.screens.sendResetLinkScreen

sealed class SendResetLinkState {

    data object Success : SendResetLinkState()
    data class Error(val message: String) : SendResetLinkState()
    data object Loading : SendResetLinkState()
}