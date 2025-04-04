package com.example.vetclinic.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class AppointmentWithDetails(
    val id: String,
    val userId: String,
    val petId: String,
    val doctorId: String,
    val serviceId: String,
    val dateTime: String,
    val status: AppointmentStatus,
    val isArchived: Boolean,
    val isConfirmed: Boolean = false,
    val serviceName: String,
    val doctorName: String,
    val doctorRole: String,
    val petName: String,
    val userName: String,
    val petAge: String
) : Parcelable