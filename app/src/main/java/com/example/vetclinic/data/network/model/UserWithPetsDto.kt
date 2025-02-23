package com.example.vetclinic.data.network.model

import com.squareup.moshi.Json

data class UserWithPetsDto(
    @Json(name = "user") val userDTO: UserDTO,
    @Json(name = "pets") val pets: List<PetDto> = emptyList()
)