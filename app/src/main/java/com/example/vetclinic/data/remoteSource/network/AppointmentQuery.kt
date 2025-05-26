package com.example.vetclinic.data.remoteSource.network

import com.squareup.moshi.Json

data class AppointmentQuery(
    @Json(name = "user_id") val userId: String? = null
) {
}