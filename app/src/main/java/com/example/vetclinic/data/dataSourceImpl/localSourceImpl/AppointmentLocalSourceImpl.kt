package com.example.vetclinic.data.dataSourceImpl.localSourceImpl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.vetclinic.data.DataSourceUtils
import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.data.localSource.database.models.AppointmentWithDetailsDbModel
import com.example.vetclinic.data.localSource.interfaces.AppointmentLocalSource
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.remoteSource.AppointmentRemoteMediator
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.utils.AgeUtils
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class AppointmentLocalSourceImpl @Inject constructor(
    private val vetClinicDao: VetClinicDao,
    private val ageUtils: AgeUtils,
    private val supabaseApiService: SupabaseApiService,
    private val appointmentMapper: AppointmentMapper,
) : AppointmentLocalSource {

    @OptIn(ExperimentalPagingApi::class)
    override fun getAppointmentsByDate(
        date: String,
    ): Flow<PagingData<AppointmentWithDetailsDbModel>> {

        val pagingSourceFactory = { vetClinicDao.observeAppointmentsPaging(date) }

        return Pager(
            config = PagingConfig(pageSize = 15),
            remoteMediator = AppointmentRemoteMediator(
                selectedDate = date,
                supabaseApiService = supabaseApiService,
                appointmentMapper = appointmentMapper,
                vetClinicDao = vetClinicDao,
                ageUtils = ageUtils
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    override suspend fun addAppointments(appointments: List<AppointmentWithDetailsDbModel>) {
        vetClinicDao.insertAppointments(appointments)
    }

    override fun observeAppointmentsFromRoom(userId: String): Flow<List<AppointmentWithDetailsDbModel>> {
        return vetClinicDao.observeAppointmentsByUserId(userId)
    }

    override suspend fun updateAppointmentStatusInRoom(updatedAppointment: AppointmentWithDetailsDbModel)
            : Result<Unit> {
        return DataSourceUtils.executeRoomCall { vetClinicDao.updateAppointment(updatedAppointment) }
    }
}