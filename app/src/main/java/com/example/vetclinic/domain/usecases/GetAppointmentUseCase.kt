package com.example.vetclinic.domain.usecases

import androidx.paging.PagingData
import com.example.vetclinic.domain.interfaces.AppointmentRepository
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAppointmentUseCase @Inject constructor(private val repository: AppointmentRepository) {


//    suspend fun getAppointmentsByUserIdFromRoom(userId: String): Result<List<AppointmentWithDetails>> {
//        return repository.getAppointmentsByUserIdFromRoom(userId)
//    }


    suspend fun getAppointmentsByUserIdFromSupabase(userId: String): Result<List<AppointmentWithDetails>> {
        return repository.getAppointmentsByUserId(userId)
    }


//    suspend fun getAppointmentsByDate(date: String, offset:Int, limit:Int) {
//        return repository.getAppointmentsByDate(date, offset, limit)
//    }

    suspend fun getAppointmentsByDate(
        selectedDate: String,
        nowIso: String,
    ): Flow<PagingData<AppointmentWithDetails>> {
        return repository.getAppointmentsByDate(selectedDate, nowIso)
    }

    fun observeAppointmentsInRoomByUserId(userId: String): Flow<List<AppointmentWithDetails>> {
        return repository.observeAppointmentsFromRoom(userId)
    }
}