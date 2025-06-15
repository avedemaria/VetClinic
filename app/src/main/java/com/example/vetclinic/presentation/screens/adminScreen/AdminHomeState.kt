package com.example.vetclinic.presentation.screens.adminScreen

import androidx.paging.PagingData
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails

sealed class AdminHomeState {

    data object Empty : AdminHomeState()
    data object Loading : AdminHomeState()
    data object Error: AdminHomeState()
    data class Success(
        val appointments: PagingData<AppointmentWithDetails>,
        val selectedDate: String,
    ) : AdminHomeState()

    data object LoggedOut : AdminHomeState()

}