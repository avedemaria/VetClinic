package com.example.vetclinic.data.network.model

import com.example.vetclinic.utils.extractTime
import com.squareup.moshi.Json

data class TimeSlotDto(
    @Json(name = "id") val id: String,
    @Json(name = "doctor_id") val doctorId: String,
    @Json(name = "service_id") val serviceId: String,
    @Json(name = "start_time") val startTime: String,
    @Json(name = "end_time") val endTime: String,
    @Json(name = "is_booked") val isBooked: Boolean,
    @Json(name = "day_id") val dayId: String
) {

    val formattedStartTime: String
        get() = startTime.extractTime()


}



