package com.example.vetclinic.presentation.adapter.timeSlotsAdapter

import com.example.vetclinic.domain.entities.timeSlot.TimeSlot

interface OnTimeSlotClickedListener {

    fun onTimeSlotClicked (timeSlot: TimeSlot)
}