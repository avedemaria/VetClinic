package com.example.vetclinic.data.mapper

import android.util.Log
import com.example.vetclinic.data.database.model.AppointmentWithDetailsDbModel
import com.example.vetclinic.data.network.model.AppointmentDto
import com.example.vetclinic.data.network.model.AppointmentWithDetailsDto
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


    fun appointmentWithDetailsToAppointmentWithDetailsDbModel(entity: AppointmentWithDetails)
            : AppointmentWithDetailsDbModel {
        return AppointmentWithDetailsDbModel(
            id = entity.id,
            userId = entity.userId,
            petId = entity.petId,
            doctorId = entity.doctorId,
            serviceId = entity.serviceId,
            dateTime = entity.dateTime,
            status = entity.status.toString(),
            isArchived = entity.isArchived,
            isConfirmed = entity.isConfirmed,
            serviceName = entity.serviceName,
            doctorName = entity.doctorName,
            doctorRole = entity.doctorRole,
            petName = entity.petName,
            userName = entity.userName,
            userLastName = entity.userLastName,
            petAge = entity.petAge
        )
    }


    fun appointmentWithDetailsDtoToAppointmentWithDetails(dto: AppointmentWithDetailsDto): AppointmentWithDetails {
        return AppointmentWithDetails(
            id = dto.id,
            userId = dto.userId,
            petId = dto.petId,
            doctorId = dto.doctorId,
            serviceId = dto.serviceId,
            dateTime = dto.dateTime,
            status = dto.status.toStatusEnum(),
            isArchived = dto.isArchived,
            isConfirmed = dto.isConfirmed,
            serviceName = dto.serviceName,
            doctorName = dto.doctorName,
            doctorRole = dto.doctorRole,
            petName = dto.petName,
            userName = dto.userName,
            userLastName = dto.userLastName,
            petAge = dto.petAge

        )
    }

    fun appointmentWithDetailsDtoToAppointmentWithDetailsDbModel(dto: AppointmentWithDetailsDto):
            AppointmentWithDetailsDbModel {
        return AppointmentWithDetailsDbModel(
            id = dto.id,
            userId = dto.userId,
            petId = dto.petId,
            doctorId = dto.doctorId,
            serviceId = dto.serviceId,
            dateTime = dto.dateTime,
            status = dto.status,
            isArchived = dto.isArchived,
            isConfirmed = dto.isConfirmed,
            serviceName = dto.serviceName,
            doctorName = dto.doctorName,
            doctorRole = dto.doctorRole,
            petName = dto.petName,
            userName = dto.userName,
            userLastName = dto.userLastName,
            petAge = dto.petAge
        )
    }


    fun appointmentWithDetailsEntityToAppointmentDto(entity: AppointmentWithDetails): AppointmentDto {
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


    fun appointmentWithDetailsDbModelToEntity(dbModel: AppointmentWithDetailsDbModel):
            AppointmentWithDetails {
        return AppointmentWithDetails(
            id = dbModel.id,
            userId = dbModel.userId,
            petId = dbModel.petId,
            doctorId = dbModel.doctorId,
            serviceId = dbModel.serviceId,
            dateTime = dbModel.dateTime,
            status = dbModel.status.toStatusEnum(),
            isArchived = dbModel.isArchived,
            isConfirmed = dbModel.isConfirmed,
            serviceName = dbModel.serviceName,
            doctorName = dbModel.doctorName,
            doctorRole = dbModel.doctorRole,
            petName = dbModel.petName,
            userName = dbModel.userName,
            userLastName = dbModel.userLastName,
            petAge = dbModel.petAge
        )
    }


//    fun appointmentWithDetailsToAppointment(appointmentWithDetails: AppointmentWithDetails):
//            Appointment {
//        return Appointment(
//            id = appointmentWithDetails.id,
//            userId = appointmentWithDetails.userId,
//            petId = appointmentWithDetails.petId,
//            doctorId = appointmentWithDetails.doctorId,
//            serviceId = appointmentWithDetails.serviceId,
//            dateTime = appointmentWithDetails.dateTime,
//            status = appointmentWithDetails.status,
//            isArchived = appointmentWithDetails.isArchived,
//            isConfirmed = appointmentWithDetails.isConfirmed
//        )
//    }


    private fun String.toStatusEnum(): AppointmentStatus {
        return try {
            AppointmentStatus.valueOf(this.trim().uppercase())
        } catch (e: IllegalArgumentException) {
            AppointmentStatus.SCHEDULED
        }
    }
}