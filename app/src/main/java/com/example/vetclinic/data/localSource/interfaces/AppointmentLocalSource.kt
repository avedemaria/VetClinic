package com.example.vetclinic.data.localSource.interfaces

import androidx.paging.PagingData
import com.example.vetclinic.data.localSource.database.models.AppointmentWithDetailsDbModel
import kotlinx.coroutines.flow.Flow

interface AppointmentLocalSource {

   suspend fun addAppointments(appointments: List<AppointmentWithDetailsDbModel>)

    fun getAppointmentsByDate(
        date: String,
    ): Flow<PagingData<AppointmentWithDetailsDbModel>>


    fun observeAppointmentsFromRoom(userId: String): Flow<List<AppointmentWithDetailsDbModel>>

    suspend fun updateAppointmentStatusInRoom(updatedAppointment: AppointmentWithDetailsDbModel)
    : Result<Unit>

}