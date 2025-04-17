package com.example.vetclinic.presentation.sendResetLinkScreen

sealed class SendResetLinkState {

    data object Success : SendResetLinkState()
    data class Error(val message: String) : SendResetLinkState()
    data object Loading : SendResetLinkState()
}