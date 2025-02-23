package com.example.vetclinic.data.network.model

import com.squareup.moshi.Json

data class PetDto(
    @Json(name = "pet_id") val petId: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "pet_name") val petName: String,
    @Json(name = "pet_type") val petType: String?,
    @Json(name = "pet_age") val petAge: String?
)