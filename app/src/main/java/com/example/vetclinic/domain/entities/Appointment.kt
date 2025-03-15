package com.example.vetclinic.domain.entities

import android.os.Parcelable
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class Appointment(
    val id: String,
    val userId: String,
    val petId: String,
    val doctorId: String,
    val serviceId: String,
    val status: AppointmentStatus,
    val owner: User,
    val pet: Pet,
    val doctor: Doctor,
    val service: Service
) : Parcelable
