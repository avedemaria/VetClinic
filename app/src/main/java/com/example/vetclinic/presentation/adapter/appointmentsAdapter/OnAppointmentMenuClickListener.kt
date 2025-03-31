package com.example.vetclinic.presentation.adapter.appointmentsAdapter

import com.example.vetclinic.domain.entities.AppointmentWithDetails

interface OnAppointmentMenuClickListener {

    fun onAppointmentMenuClicked(appointment: AppointmentWithDetails)
}