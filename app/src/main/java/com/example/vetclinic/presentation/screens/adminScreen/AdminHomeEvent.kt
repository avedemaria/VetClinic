package com.example.vetclinic.presentation.screens.adminScreen

import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails

sealed class AdminHomeEvent {

    data class OnBellClicked(val appointment: AppointmentWithDetails) : AdminHomeEvent()
}