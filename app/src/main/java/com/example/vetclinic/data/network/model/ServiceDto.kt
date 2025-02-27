package com.example.vetclinic.data.network.model

import com.squareup.moshi.Json

data class ServiceDto(
    @Json(name = "id") val id: String,
    @Json(name = "department_id") val departmentId: String,
    @Json(name = "price") val price: String,
    @Json(name = "name") val serviceName: String,
    @Json(name = "duration") val duration: Int
)