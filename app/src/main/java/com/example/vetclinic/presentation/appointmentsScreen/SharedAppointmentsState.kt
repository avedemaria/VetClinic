package com.example.vetclinic.presentation.appointmentsScreen

import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import java.time.LocalDate

sealed class SharedAppointmentsState {
    data object Loading : SharedAppointmentsState()
    data class Success(
        val appointments: List<AppointmentWithDetails>,
        val selectedDate: LocalDate? = null,
    ) : SharedAppointmentsState()

    data class Error(val message: String) : SharedAppointmentsState()
    data object Empty : SharedAppointmentsState()
}