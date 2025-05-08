package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.bookAppointmentFragment

import com.example.vetclinic.domain.entities.timeSlot.Day
import com.example.vetclinic.domain.entities.timeSlot.DayWithTimeSlots
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.timeSlot.TimeSlot

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

    object AppointmentAdded: BookAppointmentState()
}