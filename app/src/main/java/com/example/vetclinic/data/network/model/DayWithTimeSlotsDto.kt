package com.example.vetclinic.data.network.model

import com.squareup.moshi.Json
import com.example.vetclinic.presentation.utils.extractDayOfMonth

data class DayWithTimeSlotsDto(

    @Json(name = "id") val id: String,
    @Json(name = "date") val date: String,
    @Json(name = "time_slots") val timeSlots: List<TimeSlotDto>
) {
    val dayOfMonth: Int
        get() = date.extractDayOfMonth()
}


//fun String.extractDayOfMonth(): Int {
//    val parsedDate = LocalDate.parse(this)
//    return parsedDate.dayOfMonth
//}