package com.example.vetclinic.domain.interfaces

import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails

interface ReminderRepository {

   suspend fun scheduleReminder(appointments: List<AppointmentWithDetails>)
}