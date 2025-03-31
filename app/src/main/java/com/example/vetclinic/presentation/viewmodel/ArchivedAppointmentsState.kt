package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.AppointmentWithDetails
import java.time.LocalDate

sealed class ArchivedAppointmentsState {
    object Empty : ArchivedAppointmentsState()
    object Loading : ArchivedAppointmentsState()
    data class Error(val message: String) : ArchivedAppointmentsState()
    data class Success(
        val appointments: List<AppointmentWithDetails>,
        val selectedDate: LocalDate?
    ) : ArchivedAppointmentsState()
}