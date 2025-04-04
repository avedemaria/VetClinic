package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.AppointmentWithDetails
import java.time.LocalDate

sealed class SharedAppointmentsState {
    object Loading : SharedAppointmentsState()
    data class Success(
        val appointments: List<AppointmentWithDetails>,
        val selectedDate: LocalDate? = null,
    ) : SharedAppointmentsState()

    data class Error(val message: String) : SharedAppointmentsState()
    object Empty : SharedAppointmentsState()
}