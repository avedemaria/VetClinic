package com.example.vetclinic.presentation.adapter.doctorsAdapter

import com.example.vetclinic.domain.entities.Doctor

interface OnAppointmentClickListener {


    fun onBookButtonClick(doctor: Doctor)
}