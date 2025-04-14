package com.example.vetclinic.presentation.viewmodel.admin

import androidx.paging.PagingData
import com.example.vetclinic.domain.entities.AppointmentWithDetails

sealed class AdminHomeState {

    data object Empty : AdminHomeState()
    data object Loading : AdminHomeState()
//    data object Reset: AdminHomeState()
    data class Error(val message: String) : AdminHomeState()
    data class Success(
        val appointments: PagingData<AppointmentWithDetails>,
        val selectedDate: String,
    ) : AdminHomeState()

    data object LoggedOut : AdminHomeState()

}