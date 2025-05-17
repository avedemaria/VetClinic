package com.example.vetclinic.data.network.model

import com.squareup.moshi.Json

data class DayWithTimeSlotsDto(

    @Json(name = "id") val id: String,
    @Json(name = "date") val date: String,
    @Json(name = "time_slots") val timeSlots: List<TimeSlotDto>
)


