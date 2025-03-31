package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.AppointmentWithDetails
import java.time.LocalDate

sealed class AdminHomeState {

    object Empty : AdminHomeState()
    object Loading : AdminHomeState()
    data class Error(val message: String) : AdminHomeState()
    data class Success(
        val appointments: List<AppointmentWithDetails>,
        val selectedDate: LocalDate?
    ) : AdminHomeState()

}