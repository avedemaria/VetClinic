package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.AppointmentWithDetails

sealed class CurrentAppointmentsState {


    object Loading : CurrentAppointmentsState()
    data class Success(val appointments: List<AppointmentWithDetails>) : CurrentAppointmentsState()
    data class Error(val message: String) : CurrentAppointmentsState()
    object Empty : CurrentAppointmentsState()


}