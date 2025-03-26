package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.network.model.AppointmentDto
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentStatus
import jakarta.inject.Inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentMapper @Inject constructor() {


    fun appointmentDtoToAppointmentEntity(dto: AppointmentDto): Appointment {
        return Appointment(
            id = dto.id,
            userId = dto.userId,
            petId = dto.petId,
            doctorId = dto.doctorId,
            serviceId = dto.serviceId,
            dateTime = dto.dateTime.toString(),
            status = dto.status.toStatusEnum(),
            isArchived = dto.isArchived
        )
    }


    fun appointmentEntityToAppointmentDto(entity: Appointment): AppointmentDto {

        val parsedDateTime =
            LocalDateTime.parse(entity.dateTime,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))

        return AppointmentDto(
            id = entity.id,
            userId = entity.userId,
            petId = entity.petId,
            doctorId = entity.doctorId,
            serviceId = entity.serviceId,
            dateTime = parsedDateTime,
            status = entity.status.toString(),
            isArchived = entity.isArchived
        )
    }


    private fun String.toStatusEnum(): AppointmentStatus {
        return try {
            AppointmentStatus.valueOf(this.trim().uppercase())
        } catch (e: IllegalArgumentException) {
            AppointmentStatus.SCHEDULED
        }
    }
}