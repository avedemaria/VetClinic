package com.example.vetclinic.data.network.model

import com.example.vetclinic.CodeReview
import com.squareup.moshi.Json

@CodeReview("Всё ок, но точно все поля не null?")
data class DoctorDto(
    @Json(name = "uid") val uid: String,
    @Json(name = "name") val doctorName: String,
    @Json(name = "department") val department: String,
    @Json(name = "role") val role: String,
    @Json(name = "photoUrl") val photoUrl: String
)


