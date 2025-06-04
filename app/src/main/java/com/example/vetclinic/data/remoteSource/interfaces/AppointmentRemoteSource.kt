package com.example.vetclinic.data.remoteSource.interfaces

import com.example.vetclinic.data.remoteSource.network.model.AppointmentDto
import com.example.vetclinic.data.remoteSource.network.model.AppointmentWithDetailsDto
import kotlinx.coroutines.flow.Flow

interface AppointmentRemoteSource {

    suspend fun addAppointment(appointment: AppointmentDto): Result<Unit>


    suspend fun getAppointmentsByUserId(
        userId: String,
    ): Result<List<AppointmentWithDetailsDto>>


    suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentDto,
    ): Result<Unit>


    suspend fun observeAppointmentChanges(): Flow<AppointmentDto>


}