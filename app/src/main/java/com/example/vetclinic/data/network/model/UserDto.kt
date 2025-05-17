package com.example.vetclinic.data.network.model

import com.squareup.moshi.Json

data class UserDto(
    @Json(name = "uid") val uid: String,
    @Json(name = "user_name") val userName: String,
    @Json(name = "user_last_name") val userLastName: String,
    @Json(name = "phone_number") val phoneNumber: String,
    @Json(name = "email") val email: String,
    @Json(name= "role") val role:String
)
