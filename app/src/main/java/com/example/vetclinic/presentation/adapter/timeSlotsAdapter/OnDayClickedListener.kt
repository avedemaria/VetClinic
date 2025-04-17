package com.example.vetclinic.presentation.adapter.timeSlotsAdapter

import com.example.vetclinic.domain.entities.timeSlot.Day

interface OnDayClickedListener {


    fun onDayClicked(day: Day)
}