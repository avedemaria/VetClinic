package com.example.vetclinic.domain

import com.example.vetclinic.domain.entities.Appointment

interface AppointmentRepository {

    suspend fun addAppointmentToSupabaseDb(appointment: Appointment): Result<Unit>
}