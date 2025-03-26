package com.example.vetclinic.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DayWithTimeSlots(
    val day: Day,
    val timeSlots: List<TimeSlot>
) : Parcelable {

}