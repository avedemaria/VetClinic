package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import android.view.View
import android.widget.TextView

interface AppointmentViewBinding {

    val root: View
    val tvPetName: TextView
    val tvOwnerName: TextView
    val tvDoctorName: TextView
    val tvServiceName: TextView
    val tvTime: TextView
    val tvPetAge: TextView
    val tvStatus: TextView
    val ivBell: View
}