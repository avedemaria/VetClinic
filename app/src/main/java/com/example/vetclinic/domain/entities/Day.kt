package com.example.vetclinic.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Time


@Parcelize
data class Day(
    val id: String,
    val date: String,
    val timeSlots: List<TimeSlot>,
    val isSelected: Boolean = false,

    ) : Parcelable {
}