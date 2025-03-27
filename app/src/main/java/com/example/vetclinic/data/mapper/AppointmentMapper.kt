package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.network.model.AppointmentDto
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentStatus
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.formatToLocalDateTime
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

        return AppointmentDto(
            id = entity.id,
            userId = entity.userId,
            petId = entity.petId,
            doctorId = entity.doctorId,
            serviceId = entity.serviceId,
            dateTime = entity.dateTime,
            status = entity.status.toString(),
            isArchived = entity.isArchived
        )
    }


    fun appointmentToAppointmentWithDetails(
        appointment: Appointment,
        serviceName: String,
        doctorName: String,
        doctorRole: String,
        userName: String,
        petName: String
    ): AppointmentWithDetails {
        return AppointmentWithDetails(
            id = appointment.id,
            userId = appointment.userId,
            petId = appointment.petId,
            doctorId = appointment.doctorId,
            serviceId = appointment.serviceId,
            dateTime = appointment.dateTime,
            status = appointment.status,
            isArchived = appointment.isArchived,
            serviceName = serviceName,
            doctorName = doctorName,
            doctorRole = doctorRole,
            petName = petName,
            userName = userName
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