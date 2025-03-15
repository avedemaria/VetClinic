package com.example.vetclinic.data.network.model

import com.example.vetclinic.domain.entities.Service
import com.squareup.moshi.Json
import kotlinx.datetime.LocalDateTime

data class AppointmentDto(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "pet_id") val petId: String,
    @Json(name = "doctor_id") val doctorId: String,
    @Json(name = "service_id") val serviceId: String,
    @Json(name = "date_time") val dateTime: LocalDateTime,
    @Json(name = "status") val status: String,
    @Json(name = "users") val userDto: UserDTO,
    @Json(name = "pets") val petDto: PetDto,
    @Json(name = "doctors") val doctorDto: DoctorDto,
    @Json(name = "services") val serviceDto: ServiceDto

)

