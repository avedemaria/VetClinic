package com.example.vetclinic.domain.usecases

import android.util.Log
import com.example.vetclinic.domain.interfaces.AppointmentRepository
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import jakarta.inject.Inject

class UpdateAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository,
) {
    suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentWithDetails,
    ): Result<Unit> {
        return repository.updateAppointmentStatus(updatedAppointment)
    }


    suspend fun updateAppointmentInRoom(appointment: AppointmentWithDetails) {
        repository.updateAppointmentStatusInRoom(appointment)
    }

    suspend fun subscribeToAppointmentChanges(callback: (Appointment) -> Unit) {
        Log.d("UpdateAppointmentUseCase", "subscribed to channel")
        repository.subscribeToAppointmentChanges(callback)
    }


//    suspend fun subscribeToAppointmentChanges(): Flow<Appointment> {
//        Log.d("UpdateAppointmentUseCase", "subscribed to channel")
//        return repository.subscribeToAppointmentChanges()
//    }

    suspend fun unsubscribeFromAppointmentChanges() {
        repository.unsubscribeFromAppointmentChanges()
    }
}
