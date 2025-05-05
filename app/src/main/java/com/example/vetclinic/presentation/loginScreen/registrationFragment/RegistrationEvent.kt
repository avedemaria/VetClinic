package com.example.vetclinic.presentation.loginScreen.registrationFragment

sealed class RegistrationEvent {

    data class ShowToast(val message: String) : RegistrationEvent()

}