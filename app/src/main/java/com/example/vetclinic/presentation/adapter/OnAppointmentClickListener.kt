package com.example.vetclinic.presentation.adapter

import com.example.vetclinic.domain.selectDoctorFeature.Doctor

interface OnAppointmentClickListener {


    fun onBookButtonClick(doctor: Doctor)
}