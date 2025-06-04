package com.example.vetclinic.data.remoteSource.network.model

import com.squareup.moshi.Json

data class CanBookTimeSlotParams(
    @Json(name = "p_doctor_id") val doctorId: String,
    @Json(name = "p_start_time") val startTime: String,
    @Json(name = "p_end_time") val endTime: String,
)