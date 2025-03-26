package com.example.vetclinic.data

import android.util.Log
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.AppointmentRepository
import com.example.vetclinic.domain.entities.Appointment
import jakarta.inject.Inject


class AppointmentRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val appointmentMapper: AppointmentMapper
) : AppointmentRepository {

    override suspend fun addAppointmentToSupabaseDb(appointment: Appointment): Result<Unit> =
        kotlin.runCatching {

            val appointmentDto = appointmentMapper.appointmentEntityToAppointmentDto(appointment)

            val response = supabaseApiService.addAppointment(appointmentDto)
            if (response.isSuccessful) {
                Log.d(TAG, "Successfully added appointment to Supabase DB")
                Unit
            } else {
                throw Exception("Failed to add appointment ${response.code()} " +
                        "- ${response.errorBody()}")
            }
        }
            .onFailure { e ->
                Log.e(TAG, "Error while adding appointment to Supabase $e", e)
            }


    companion object {
        private const val TAG = "AppointmentRepositoryImpl"
    }
}