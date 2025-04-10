package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.ReminderRepository
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import jakarta.inject.Inject

class AppointmentReminderUseCase @Inject constructor(
    private val repository: ReminderRepository,
) {

    operator fun invoke(appointments: List<AppointmentWithDetails>) {
        repository.scheduleReminder(appointments)
    }
}