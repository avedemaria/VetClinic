package com.example.vetclinic.data.network.model

import com.squareup.moshi.Json

data class DayDto(
    @Json(name = "id") val id: String,
    @Json(name = "date") val date: String,
) {

}