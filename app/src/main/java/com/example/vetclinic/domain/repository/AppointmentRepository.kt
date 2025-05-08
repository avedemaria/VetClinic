package com.example.vetclinic.domain.repository

import androidx.paging.PagingData
import com.example.vetclinic.domain.entities.appointment.Appointment
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {

    suspend fun addAppointmentToSupabaseDb(appointment: Appointment): Result<Unit>


    suspend fun getAppointmentsByUserId(
        userId: String,
    ): Result<List<AppointmentWithDetails>>


     fun getAppointmentsByDate(
        date: String
    ): Flow<PagingData<AppointmentWithDetails>>

    suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentWithDetails,
    ): Result<Unit>


    suspend fun subscribeToAppointmentChanges(callback: (Appointment) -> Unit)

    suspend fun unsubscribeFromAppointmentChanges()

    fun observeAppointmentsFromRoom(userId: String): Flow<List<AppointmentWithDetails>>

    suspend fun updateAppointmentStatusInRoom(updatedAppointment: AppointmentWithDetails): Result<Unit>




}