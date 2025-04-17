package com.example.vetclinic.domain.entities.timeSlot

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate


@Parcelize
data class Day(
    val id: String,
    val date: LocalDate,
    val isSelected: Boolean = false

) : Parcelable {
}