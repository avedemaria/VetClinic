package com.example.vetclinic.domain

import androidx.paging.PagingData
import com.example.vetclinic.data.network.model.AppointmentDto
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.Service
import com.example.vetclinic.domain.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface AppointmentRepository {

    suspend fun addAppointmentToSupabaseDb(appointment: Appointment): Result<Unit>


    suspend fun getAppointmentsByUserId(
        userId: String,
    ): Result<List<AppointmentWithDetails>>

//    suspend fun getAppointmentsByDate(date: String): Result<List<AppointmentWithDetails>>

    suspend fun getAppointmentsByDate(date: String): Flow<PagingData<AppointmentWithDetails>>

    suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentWithDetails,
    ): Result<Unit>



    suspend fun getPetFromRoomById(petId: String): Pet

    suspend fun getUserFromRoomById(userId: String): User

    suspend fun subscribeToAppointmentChanges(callback: (Appointment) -> Unit)

//    suspend fun subscribeToAppointmentChanges(): Flow<Appointment>

    suspend fun unsubscribeFromAppointmentChanges()
//
//    suspend fun getAppointmentsByUserIdFromRoom(userId: String): Result<List<AppointmentWithDetails>>

    fun observeAppointmentsFromRoom(userId: String): Flow<List<AppointmentWithDetails>>

//    suspend fun getAppointmentsByDateFromRoom(date: String): Result<List<AppointmentWithDetails>>


}