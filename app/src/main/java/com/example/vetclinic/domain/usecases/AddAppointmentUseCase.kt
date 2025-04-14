package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.AppointmentRepository
import com.example.vetclinic.domain.entities.Appointment
import jakarta.inject.Inject

class AddAppointmentUseCase @Inject constructor(private val repository: AppointmentRepository) {


    suspend fun addAppointment(appointment: Appointment): Result<Unit> {
        return repository.addAppointmentToSupabaseDb(appointment)
    }
}