package com.example.vetclinic.presentation.registrationScreen

sealed class RegistrationEvent {

    data class ShowToast(val message: String) : RegistrationEvent()

}