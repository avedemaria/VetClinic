package com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl

import com.example.vetclinic.data.DataSourceUtils
import com.example.vetclinic.data.remoteSource.interfaces.AppointmentRemoteSource
import com.example.vetclinic.data.remoteSource.network.AppointmentQuery
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.data.remoteSource.network.model.AppointmentDto
import com.example.vetclinic.data.remoteSource.network.model.AppointmentWithDetailsDto
import com.example.vetclinic.domain.entities.appointment.Appointment
import com.squareup.moshi.Moshi
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.retry
import kotlinx.io.IOException
import timber.log.Timber
import java.net.SocketException

class AppointmentRemoteSourceImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val supabaseClient: SupabaseClient,
    private val moshi: Moshi,
) : AppointmentRemoteSource {

    private var subscription: RealtimeChannel? = null


    override suspend fun addAppointment(appointment: AppointmentDto): Result<Unit> {
        return DataSourceUtils.executeApiCall { supabaseApiService.addAppointment(appointment) }
    }


    override suspend fun getAppointmentsByUserId(
        userId: String,
    ): Result<List<AppointmentWithDetailsDto>> {
        return runCatching {
            val query = AppointmentQuery(userId = userId)
            val response = supabaseApiService.getAppointmentWithDetails(query)

            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    "API error ${response.code()} - ${response.message()} - $errorBody"
                throw Exception(errorMessage)
            }
        }.onFailure { e ->
            Timber.e(e, "Failed to fetch appointments from API for userId=$userId")
        }
    }


    override suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentDto,
    ): Result<Unit> {
        return DataSourceUtils.executeUnitApiCall {
            supabaseApiService.updateAppointmentStatus(
                "eq.${updatedAppointment.id}",
                updatedAppointment
            )
        }
    }

    override suspend fun observeAppointmentChanges(): Flow<AppointmentDto> {
        val adapter = moshi.adapter(AppointmentDto::class.java)

        val channel = supabaseClient.channel(CHANNEL_ID)
        channel.subscribe()
        subscription = channel

        return channel
            .postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                table = "appointments"
            }
            .retry { e ->
                Timber.e(e, "Retrying after error: ${e.message}")
                e is IOException || e is SocketException
            }
            .mapNotNull { change ->
                runCatching {
                    val rawJson = change.record.toString()
                    adapter.fromJson(rawJson)
                }.onFailure {
                    Timber.e(it, "Failed to parse AppointmentDto from record")
                }.getOrNull()
            }
            .catch { e ->
                Timber.e(e, "Error in appointment change flow: ${e.message}")
            }

    }

    override suspend fun unsubscribeFromAppointmentChanges() {
        subscription?.unsubscribe()
        subscription = null
        Timber.tag(TAG).d("unsubscribed")
    }


    companion object {
        private const val CHANNEL_ID = "appointments"
        private const val TAG = "AppointmentRemoteSourceImpl"
    }
}