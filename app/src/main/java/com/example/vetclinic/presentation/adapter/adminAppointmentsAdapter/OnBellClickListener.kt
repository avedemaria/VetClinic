package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails

interface OnBellClickListener {

    fun onBellClicked (appointment: AppointmentWithDetails)
}