package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.domain.repository.ReminderRepository
import jakarta.inject.Inject

class AppointmentReminderUseCase @Inject constructor(
    private val repository: ReminderRepository,
) {
    suspend operator fun invoke(appointments: List<AppointmentWithDetails>) {
        repository.scheduleReminder(appointments)
    }
}