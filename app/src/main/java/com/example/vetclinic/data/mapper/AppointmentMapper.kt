package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.network.model.AppointmentDto
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentStatus
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import jakarta.inject.Inject

class AppointmentMapper @Inject constructor() {


    fun appointmentDtoToAppointmentEntity(dto: AppointmentDto): Appointment {
        return Appointment(
            id = dto.id,
            userId = dto.userId,
            petId = dto.petId,
            doctorId = dto.doctorId,
            serviceId = dto.serviceId,
            dateTime = dto.dateTime,
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
            isArchived = entity.isArchived,
            isConfirmed = entity.isConfirmed
        )
    }



    fun appointmentToAppointmentWithDetails(
        appointment: Appointment,
        serviceName: String,
        doctorName: String,
        doctorRole: String,
        userName: String,
        petName: String,
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
            isConfirmed = appointment.isConfirmed,
            serviceName = serviceName,
            doctorName = doctorName,
            doctorRole = doctorRole,
            petName = petName,
            userName = userName
        )
    }


    fun appointmentWithDetailsToAppointment(appointmentWithDetails: AppointmentWithDetails):
            Appointment {
        return Appointment(
            id = appointmentWithDetails.id,
            userId = appointmentWithDetails.userId,
            petId = appointmentWithDetails.petId,
            doctorId = appointmentWithDetails.doctorId,
            serviceId = appointmentWithDetails.serviceId,
            dateTime = appointmentWithDetails.dateTime,
            status = appointmentWithDetails.status,
            isArchived = appointmentWithDetails.isArchived,
            isConfirmed = appointmentWithDetails.isConfirmed
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