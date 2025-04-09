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
import jakarta.inject.Inject
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
    private val db: VetClinicDatabase,
    private val vetClinicDao: VetClinicDao,
    private val supabaseApiService: SupabaseApiService,
    private val appointmentMapper: AppointmentMapper
) : RemoteMediator<Int, AppointmentWithDetailsDbModel>() {

    private var nextPageKey = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AppointmentWithDetailsDbModel>
    ): MediatorResult {
        Log.d(TAG, "Load called with type: $loadType")

        return try {
            val limit = state.config.pageSize
            Log.d(TAG, "Page size: $limit")

            // Reset or advance the page based on the load type
            when (loadType) {
                LoadType.REFRESH -> {
                    Log.d(TAG, "REFRESH: Resetting nextPageKey to 0")
                    nextPageKey = 0
                }
                LoadType.PREPEND -> {
                    Log.d(TAG, "PREPEND: Returning success with endOfPaginationReached=true")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    Log.d(TAG, "APPEND: Last item = ${state.lastItemOrNull()?.id}")
                    if (state.lastItemOrNull() == null) {
                        Log.d(TAG, "APPEND: No last item, returning end of pagination")
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    Log.d(TAG, "APPEND: Using nextPageKey = $nextPageKey")
                }
            }

            val offset = nextPageKey * limit
            Log.d(TAG, "Calculated offset: $offset, limit: $limit for date: $selectedDate")

            Log.d(TAG, "Making API call with date: $selectedDate, offset: $offset, limit: $limit")
            val response = supabaseApiService.getAppointmentsWithDetailsByDate(selectedDate, offset, limit)
            Log.d(TAG, "API response code: ${response.code()}")

            if (response.isSuccessful) {
                val dtos = response.body().orEmpty()
                Log.d(TAG, "Received ${dtos.size} appointments from API")

                val dbModels = dtos.map {
                    appointmentMapper.appointmentWithDetailsDtoToAppointmentWithDetailsDbModel(it)
                }

                db.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        Log.d(TAG, "Clearing all appointments from DB")
                        vetClinicDao.clearAllAppointments()
                    }
                    Log.d(TAG, "Inserting ${dbModels.size} appointments into DB")
                    vetClinicDao.insertAppointments(dbModels)
                }

                // Advance the page key only if we got data
                if (dtos.isNotEmpty()) {
                    nextPageKey++
                    Log.d(TAG, "Advanced nextPageKey to $nextPageKey")
                } else {
                    Log.d(TAG, "No data received, not advancing nextPageKey")
                }

                Log.d(TAG, "Returning success with endOfPaginationReached=${dtos.isEmpty()}")
                MediatorResult.Success(endOfPaginationReached = dtos.isEmpty())
            } else {
                Log.d(TAG, "API error: ${response.code()} - ${response.message()}")
                MediatorResult.Error(
                    Exception(
                        "Server's error: ${response.code()} - ${response.message()}"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during load", e)
            MediatorResult.Error(e)
        }
    }

    companion object {
        private const val TAG = "AppointmentRemoteMediator"
    }
}

