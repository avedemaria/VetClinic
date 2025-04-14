//package com.example.vetclinic.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.vetclinic.data.database.model.AppointmentWithDetailsDbModel
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.database.model.VetClinicDatabase
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.data.network.model.AppointmentWithDetailsDto
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import retrofit2.HttpException
import retrofit2.Response
import java.time.format.DateTimeFormatter

//
//@OptIn(ExperimentalPagingApi::class)
//class AppointmentRemoteMediator @Inject constructor(
//    private val selectedDate: String,
//    private val db: VetClinicDatabase,
//    private val vetClinicDao: VetClinicDao,
//    private val supabaseApiService: SupabaseApiService,
//    private val appointmentMapper: AppointmentMapper,
//
//
//    ) : RemoteMediator<Int, AppointmentWithDetailsDbModel>() {
//
//
//    private var nextPageKey = 0
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, AppointmentWithDetailsDbModel>,
//    ): MediatorResult {
//        return try {
//
//            Log.d("AppointmentMediator", "Loading with type: $loadType, nextPageKey: $nextPageKey")
//            val limit = state.config.pageSize
//            when (loadType) {
//                LoadType.REFRESH -> nextPageKey = 0
//                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
//                LoadType.APPEND -> {
//                    if (state.lastItemOrNull() == null) {
//                        return MediatorResult.Success(endOfPaginationReached = true)
//                    }
//                    nextPageKey
////                    val lastItem = state.lastItemOrNull()
////                    if (lastItem == null) 0 else (lastItem.id.toInt() / limit) + 1
//                }
//            }
//
//            val offset = nextPageKey * limit
//            val response =
//                supabaseApiService.getAppointmentsWithDetailsByDate(selectedDate, offset, limit)
//
//            if (response.isSuccessful) {
//                val dtos = response.body().orEmpty()
//                val dbModels = dtos.map {
//                    appointmentMapper.appointmentWithDetailsDtoToAppointmentWithDetailsDbModel(it)
//                }
//                db.withTransaction {
//                    if (loadType == LoadType.REFRESH) {
//                        vetClinicDao.clearAllAppointments()
//                    }
//                    vetClinicDao.insertAppointments(dbModels)
//                }
//
//                if (dtos.isNotEmpty()) {
//                    nextPageKey++
//                }
//
//                MediatorResult.Success(endOfPaginationReached = dtos.isEmpty())
//            } else {
//                MediatorResult.Error(
//                    Exception(
//                        "Server's error: ${response.code()} " +
//                                "- ${response.message()}"
//                    )
//                )
//            }
//        } catch (e: Exception) {
//            MediatorResult.Error(e)
//        }
//
//    }
//
//
//}
//
//
@OptIn(ExperimentalPagingApi::class)
class AppointmentRemoteMediator @Inject constructor(
    private val selectedDate: String,
    private val supabaseApiService: SupabaseApiService,
    private val appointmentMapper: AppointmentMapper,
    private val vetClinicDao: VetClinicDao,
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
            val appointmentsDb = appointmentsDto.map { // 👈 лог 2
                appointmentMapper.appointmentWithDetailsDtoToAppointmentWithDetailsDbModel(it)
            }
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


    companion object {
        private const val TAG = "AppointmentRemoteMediator"
    }



}


