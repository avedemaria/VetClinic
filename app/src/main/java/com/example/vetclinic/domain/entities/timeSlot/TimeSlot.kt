package com.example.vetclinic.domain.entities.timeSlot

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeSlot(
    val id: String,
    val doctorId: String,
    val serviceId: String,
    val dayId: String,
    val startTime: String,
    val endTime: String,
    val isBooked: Boolean,
    val isSelected: Boolean = false

) : Parcelable {


}