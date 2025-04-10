package com.example.vetclinic.domain

import com.example.vetclinic.domain.entities.AppointmentWithDetails

interface ReminderRepository {

    fun scheduleReminder(appointments: List<AppointmentWithDetails>)
}