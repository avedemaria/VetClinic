package com.example.vetclinic.domain.usecases

import android.util.Log
import androidx.paging.PagingData
import com.example.vetclinic.domain.entities.appointment.Appointment
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.domain.repository.AppointmentRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow


class AppointmentUseCase @Inject constructor(private val repository: AppointmentRepository) {


    suspend fun addAppointment(appointment: Appointment): Result<Unit> {
        return repository.addAppointmentToSupabaseDb(appointment)
    }

    suspend fun getAppointmentsByUserIdFromSupabase(userId: String): Result<List<AppointmentWithDetails>> {
        return repository.getAppointmentsByUserId(userId)
    }


    fun getAppointmentsByDate(
        selectedDate: String
    ): Flow<PagingData<AppointmentWithDetails>> {
        return repository.getAppointmentsByDate(selectedDate)
    }

    fun observeAppointmentsInRoomByUserId(userId: String): Flow<List<AppointmentWithDetails>> {
        return repository.observeAppointmentsFromRoom(userId)
    }


    suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentWithDetails,
    ): Result<Unit> {
        return repository.updateAppointmentStatus(updatedAppointment)
    }


    suspend fun updateAppointmentInRoom(appointment: AppointmentWithDetails) {
        repository.updateAppointmentStatusInRoom(appointment)
    }

    suspend fun subscribeToAppointmentChanges(callback: (Appointment) -> Unit) {
        repository.subscribeToAppointmentChanges(callback)
    }

    suspend fun unsubscribeFromAppointmentChanges() {
        repository.unsubscribeFromAppointmentChanges()
    }
}