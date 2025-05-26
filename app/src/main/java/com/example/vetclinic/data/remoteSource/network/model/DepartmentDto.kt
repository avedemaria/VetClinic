package com.example.vetclinic.data.remoteSource.network.model

import com.squareup.moshi.Json

data class DepartmentDto(
    @Json(name = "id") val departmentId: String,
    @Json(name = "name") val departmentName: String
)
