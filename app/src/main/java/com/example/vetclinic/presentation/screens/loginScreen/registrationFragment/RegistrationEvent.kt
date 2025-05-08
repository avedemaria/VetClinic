package com.example.vetclinic.presentation.screens.loginScreen.registrationFragment

sealed class RegistrationEvent {

    data class ShowToast(val message: String) : RegistrationEvent()

}