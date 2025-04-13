package com.example.vetclinic.presentation.viewmodel.registration

sealed class RegistrationEvent {

    data class ShowToast(val message: String) : RegistrationEvent()

}