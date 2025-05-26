package com.example.vetclinic.data.remoteSource.network.model

import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

data class AppointmentDto(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "pet_id") val petId: String,
    @Json(name = "doctor_id") val doctorId: String,
    @Json(name = "service_id") val serviceId: String,
    @Json(name = "date_time") val dateTime: String,
    @Json(name = "status") val status: String,
    @Json(name = "is_archived") val isArchived: Boolean,
    @Json(name = "is_confirmed") val isConfirmed: Boolean

)

