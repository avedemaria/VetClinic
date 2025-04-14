package com.example.vetclinic.presentation.viewmodel.admin

import com.example.vetclinic.domain.entities.AppointmentWithDetails

sealed class AdminHomeEvent {

    data class OnBellClicked(val appointment:AppointmentWithDetails) : AdminHomeEvent()
}