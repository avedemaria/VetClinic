package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.AppointmentWithDetails
import java.time.LocalDate


//    object Loading : AppointmentsState()
//    data class Success(val appointments: List<AppointmentWithDetails>) : AppointmentsState()
//    data class Error(val message: String) : AppointmentsState()
//    object Empty : AppointmentsState()
//

    sealed class AppointmentsState {
        object Loading : AppointmentsState()
        data class Success(
            val appointments: List<AppointmentWithDetails>
        ) : AppointmentsState()
        data class Error(val message: String) : AppointmentsState()

    }

