package com.example.vetclinic.domain

import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.Service
import com.example.vetclinic.domain.entities.User
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {

    suspend fun addAppointmentToSupabaseDb(appointment: Appointment): Result<Unit>


    suspend fun getAppointmentsByUserId(
        userId: String,
        isArchived: Boolean
    ): Result<List<AppointmentWithDetails>>

    suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentWithDetails
    ): Result<Unit>

    suspend fun getDoctorById(doctorId: String): Doctor

    suspend fun getServiceById(serviceId: String): Service

    suspend fun getPetFromRoomById(petId: String): Pet

    suspend fun getUserFromRoomById(userId: String): User

    suspend fun subscribeToAppointmentChanges (): Flow<List<AppointmentWithDetails>>

    suspend fun unsubscribeFromAppointmentChanges ()


}