package com.example.vetclinic.presentation.screens.sendResetLinkScreen

sealed class SendResetLinkState {

    data object Success : SendResetLinkState()
    data object Error : SendResetLinkState()
    data object Loading : SendResetLinkState()
}