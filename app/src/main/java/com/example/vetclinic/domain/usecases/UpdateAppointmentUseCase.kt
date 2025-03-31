package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.AppointmentRepository
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import jakarta.inject.Inject

class UpdateAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentWithDetails
    ): Result<Unit> {
        return repository.updateAppointmentStatus( updatedAppointment)
    }

//    suspend fun subscribeToAppointmentChanges(): Result<Unit> {
//        return repository.subscribeToAppointmentChanges()
//    }
}