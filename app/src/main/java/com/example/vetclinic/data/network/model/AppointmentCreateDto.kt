package com.example.vetclinic.data.network.model

import com.squareup.moshi.Json

data class AppointmentCreateDto(
    @Json(name = "user_id") val userId: String,
    @Json(name = "pet_id") val petId: String,
    @Json(name = "doctor_id") val doctorId: String,
    @Json(name = "service_id") val serviceId: String,
    @Json(name = "date_time") val dateTime: String,
    @Json(name = "status") val status: String
)