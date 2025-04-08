package com.example.vetclinic.domain.usecases

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.example.vetclinic.domain.AppointmentRepository
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class GetAppointmentUseCase @Inject constructor(private val repository: AppointmentRepository) {


//    suspend fun getAppointmentsByUserIdFromRoom(userId: String): Result<List<AppointmentWithDetails>> {
//        return repository.getAppointmentsByUserIdFromRoom(userId)
//    }


    suspend fun getAppointmentsByUserIdFromSupabase(userId: String): Result<List<AppointmentWithDetails>> {
        return repository.getAppointmentsByUserId(userId)
    }


//    suspend fun getAppointmentsByDate(date: String): Result<List<AppointmentWithDetails>> {
//        return repository.getAppointmentsByDate(date)
//    }

//    suspend fun getAppointmentsByDate(selectedDate: String): Flow<PagingData<AppointmentWithDetails>> {
//        return repository.getAppointmentsByDate(selectedDate)
//    }

   fun observeAppointmentsInRoomByUserId(userId: String): Flow<List<AppointmentWithDetails>> {
        return repository.observeAppointmentsFromRoom(userId)
    }
}