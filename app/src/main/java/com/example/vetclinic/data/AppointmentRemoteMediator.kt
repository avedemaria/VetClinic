//package com.example.vetclinic.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import okio.IOException
import retrofit2.HttpException

//@OptIn(ExperimentalPagingApi::class)
//class AppointmentRemoteMediator(
//    private val vetClinicDao: VetClinicDao,
//    private val supabaseApiService: SupabaseApiService,
//) : RemoteMediator<Int, AppointmentWithDetails>() {
////
////    @OptIn(ExperimentalPagingApi::class)
////    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, AppointmentWithDetails>,
//    ): MediatorResult {
//        return try {
//            val loadKey = when (loadType) {
//                LoadType.REFRESH -> 1
//                LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
//                LoadType.APPEND -> {
//                    val lastItem = state.lastItemOrNull()
//                    if(lastItem == null) {
//                        1
//                    } else {
//                        (lastItem.id / state.config.pageSize) + 1
//                    }
//                }
//            }
//            val appointments = supabaseApiService.getAppointmentsByDate()
//        } catch (e: IOException) {
//            MediatorResult.Error(e)
//        } catch (e: HttpException) {
//            MediatorResult.Error(e)
//
//        }
//    }



