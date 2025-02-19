package com.example.vetclinic.data.network.model

import com.squareup.moshi.Json

data class DoctorDto(
    @Json(name = "uid") val uid: String,
    @Json(name = "name") val doctorName: String,
    @Json(name = "last_name") val doctorLastName: String,
    @Json(name = "department") val department: String,
    @Json(name = "role") val role: String,
    @Json(name = "photoUrl") val photoUrl: String
)


