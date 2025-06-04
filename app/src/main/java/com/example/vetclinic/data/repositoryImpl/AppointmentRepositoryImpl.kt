package com.example.vetclinic.data.repositoryImpl

import androidx.paging.PagingData
import androidx.paging.map
import com.example.vetclinic.data.localSource.interfaces.AppointmentLocalSource
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.remoteSource.interfaces.AppointmentRemoteSource
import com.example.vetclinic.domain.entities.appointment.Appointment
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.domain.repository.AppointmentRepository
import io.github.jan.supabase.realtime.RealtimeChannel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber


class AppointmentRepositoryImpl @Inject constructor(
    private val remoteSource: AppointmentRemoteSource,
    private val localSource: AppointmentLocalSource,
    private val appointmentMapper: AppointmentMapper
) : AppointmentRepository {

    private var subscription: RealtimeChannel? = null

    override suspend fun subscribeToAppointmentChanges(
        callback: (Appointment) -> Unit,
    ) {
        remoteSource.observeAppointmentChanges()
            .map { dto ->
                appointmentMapper.appointmentDtoToAppointmentEntity(dto)
            }
            .collect { appointment ->
                callback(appointment)
            }
    }


    override suspend fun unsubscribeFromAppointmentChanges() {
        subscription?.unsubscribe()
        Timber.tag(TAG).d("unsubscribed")
        subscription = null
    }


    override suspend fun addAppointmentToSupabaseDb(appointment: Appointment): Result<Unit> =
        appointmentMapper.appointmentEntityToAppointmentDto(appointment).let {
            remoteSource.addAppointment(it)
        }
            .onFailure { e ->
                Timber.tag(TAG).e(e, "Error while adding appointment")
            }


    override suspend fun getAppointmentsByUserId(
        userId: String,
    ): Result<List<AppointmentWithDetails>> =

        remoteSource.getAppointmentsByUserId(userId).map { dtoList ->
            dtoList.map { dto ->
                appointmentMapper.appointmentWithDetailsDtoToAppointmentWithDetails(dto)
            }
        }.onSuccess { domainList ->
            withContext(Dispatchers.IO) {
                val dbModels = domainList.map {
                    appointmentMapper.appointmentWithDetailsToAppointmentWithDetailsDbModel(it)
                }
                localSource.addAppointments(dbModels)
            }
        }
            .onFailure { e ->
                Timber.tag(TAG).e(e, "Error while fetching appointments")
            }


    override suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentWithDetails,
    ): Result<Unit> =
        appointmentMapper.appointmentWithDetailsEntityToAppointmentDto(updatedAppointment)
            .let {
                remoteSource.updateAppointmentStatus(it)
                    .onSuccess {
                        updateAppointmentStatusInRoom(updatedAppointment).getOrThrow()
                    }
            }
            .onFailure { e ->
                Timber.tag(TAG).e(e, "Error while updating appointment status")
            }


    override fun getAppointmentsByDate(
        date: String,
    ): Flow<PagingData<AppointmentWithDetails>> {
        return localSource.getAppointmentsByDate(date)
            .map { pagingData ->
                pagingData.map { dbModel ->
                    appointmentMapper.appointmentWithDetailsDbModelToEntity(dbModel)
                }
            }
    }


    override fun observeAppointmentsFromRoom(userId: String): Flow<List<AppointmentWithDetails>> {
        return localSource.observeAppointmentsFromRoom(userId)
            .map { list ->
                list.map {
                    appointmentMapper.appointmentWithDetailsDbModelToEntity(it)
                }
            }
    }


    override suspend fun updateAppointmentStatusInRoom(updatedAppointment: AppointmentWithDetails): Result<Unit> =
        appointmentMapper.appointmentWithDetailsToAppointmentWithDetailsDbModel(
            updatedAppointment
        ).let {
            localSource.updateAppointmentStatusInRoom(it)
        }.onFailure { e ->
                Timber.tag(TAG).d(e.message.toString())
            }


    companion object {
        private const val TAG = "AppointmentRepositoryImpl"

    }
}