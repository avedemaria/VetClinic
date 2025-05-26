package com.example.vetclinic.data.remoteSource

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.vetclinic.data.localSource.database.models.AppointmentWithDetailsDbModel
import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.data.remoteSource.network.model.AppointmentWithDetailsDto
import com.example.vetclinic.utils.AgeUtils
import jakarta.inject.Inject
import kotlinx.io.IOException
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class)
class AppointmentRemoteMediator @Inject constructor(
    private val selectedDate: String,
    private val supabaseApiService: SupabaseApiService,
    private val appointmentMapper: AppointmentMapper,
    private val vetClinicDao: VetClinicDao,
    private val ageUtils: AgeUtils
) : RemoteMediator<Int, AppointmentWithDetailsDbModel>() {

    private var pageIndex = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AppointmentWithDetailsDbModel>,
    ): MediatorResult {
        return try {
            Log.d(
                TAG,
                ">>> Load triggered: loadType = $loadType, state = ${state.anchorPosition}"
            )
            val pageIndex = getPageIndex(loadType) ?: return MediatorResult.Success(
                endOfPaginationReached = true
            )
            val limit = state.config.pageSize
            val offset = pageIndex * limit

            Log.d(
                TAG,
                "Requesting page with offset = $offset, limit = $limit, date = $selectedDate"
            )

            val response = fetchAppointments(offset, limit, selectedDate)

            val appointmentsDto = response.body().orEmpty()
            Log.d(TAG, "Fetched ${appointmentsDto.size} appointments from API")

            val appointmentsDb = calculatePetAgeAndFetchAppointments(appointmentsDto)
            if (loadType == LoadType.REFRESH) {

                vetClinicDao.refresh(appointmentsDb)
                Log.d(
                    TAG, "clearing and " +
                            "Inserting ${appointmentsDb.size} appointments into DB"
                )
            } else {
                vetClinicDao.insertAppointments(appointmentsDb)
            }

            MediatorResult.Success(endOfPaginationReached = appointmentsDto.size < limit)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }


    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }

    private suspend fun fetchAppointments(
        offset: Int,
        limit: Int,
        date: String,
    ): Response<List<AppointmentWithDetailsDto>> {
        return supabaseApiService.getAppointmentsWithDetailsByDate(date, offset, limit)
    }


    private fun calculatePetAgeAndFetchAppointments (appointmentsDto: List<AppointmentWithDetailsDto>):
            List<AppointmentWithDetailsDbModel>  {
        return appointmentsDto.map { dto ->
            val calculatedPetAge = ageUtils.calculatePetAge(dto.petBday)
            appointmentMapper.appointmentWithDetailsDtoToAppointmentWithDetailsDbModel(
                dto.copy(petBday = calculatedPetAge)
            )
        }
    }

    companion object {
        private const val TAG = "com.example.vetclinic.data.remoteSource.AppointmentRemoteMediator"
    }



}


