package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.AppointmentWithDetails
import java.time.LocalDate

sealed class DetailedAppointmentsState {
    object Loading : DetailedAppointmentsState()
    data class Success(
        val appointments: List<AppointmentWithDetails>,
        val selectedDate: LocalDate? = null,
    ) : DetailedAppointmentsState()

    data class Error(val message: String) : DetailedAppointmentsState()
    object Empty : DetailedAppointmentsState()
}