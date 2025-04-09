package com.example.vetclinic.presentation.viewmodel

import androidx.paging.PagingData
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import java.time.LocalDate

sealed class AdminHomeState {

    object Empty : AdminHomeState()
    object Loading : AdminHomeState()
    data class Error(val message: String) : AdminHomeState()
    data class Success(
        val appointments: PagingData<AppointmentWithDetails>,
        val selectedDate: String,
    ) : AdminHomeState()

    object LoggedOut : AdminHomeState()

}