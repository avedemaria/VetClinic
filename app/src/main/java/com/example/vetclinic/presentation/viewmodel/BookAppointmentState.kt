package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.Day
import com.example.vetclinic.domain.entities.DayWithTimeSlots
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.TimeSlot

sealed class BookAppointmentState {

    object Loading : BookAppointmentState()

    data class Success(
        val daysWithTimeSlots: List<DayWithTimeSlots>,
        val filteredTimeSlots: List<TimeSlot>,
        val pets: List<Pet>,
        val selectedDay: Day?,
        val selectedTimeSlot: TimeSlot?
    ) :
        BookAppointmentState()

    data class Error(val message: String) : BookAppointmentState()
}