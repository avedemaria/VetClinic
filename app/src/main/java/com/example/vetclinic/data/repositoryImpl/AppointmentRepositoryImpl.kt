package com.example.vetclinic.data.repositoryImpl

import AppointmentRemoteMediator
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.database.model.VetClinicDatabase
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.network.AppointmentQuery
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.data.network.model.AppointmentDto
import com.example.vetclinic.domain.interfaces.AppointmentRepository
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.squareup.moshi.Moshi
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.withContext


class AppointmentRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val supabaseClient: SupabaseClient,
    private val vetClinicDao: VetClinicDao,
    private val appointmentMapper: AppointmentMapper,
    private val moshi: Moshi,
) : AppointmentRepository {


    private var subscription: RealtimeChannel? = null

    private val jsonAdapter = moshi.adapter(AppointmentDto::class.java)


    override suspend fun subscribeToAppointmentChanges(
        callback: (Appointment) -> Unit,
    ) {
        val channel = supabaseClient.channel("appointments")
        val changeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "appointments"
        }
        subscription = channel
        channel.subscribe()
        Log.d(TAG, "Supabase channel subscribed successfully")

        Log.d(TAG, "Starting to collect changes from Supabase")
        changeFlow
            .retry { e ->
                e is java.net.SocketException || e is java.io.IOException
            }
            .catch { e ->
                Log.e(TAG, "Error in WebSocket flow after retry: ${e.message}")
            }
            .collect { updatedValue ->
                val updatedRecord = updatedValue.record.toString()
                Log.d(TAG, "updated value^ $updatedRecord")
                try {
                    val appointmentDto = jsonAdapter.fromJson(updatedRecord)
                    appointmentDto?.let { dto ->
                        callback(appointmentMapper.appointmentDtoToAppointmentEntity(dto))
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка парсинга JSON: ${e.message}")
                }


            }
    }


    override suspend fun unsubscribeFromAppointmentChanges() {
        subscription?.unsubscribe()
        Log.d(TAG, "unsubscribed")
        subscription = null
    }


    override suspend fun addAppointmentToSupabaseDb(appointment: Appointment): Result<Unit> =
        kotlin.runCatching {

            val appointmentDto = appointmentMapper.appointmentEntityToAppointmentDto(appointment)

            val response = supabaseApiService.addAppointment(appointmentDto)
            if (response.isSuccessful) {
                Log.d(TAG, "Successfully added appointment to Supabase DB")
                Unit
            } else {
                throw Exception(
                    "Failed to add appointment ${response.code()} " +
                            "- ${response.errorBody()}"
                )
            }
        }
            .onFailure { e ->
                Log.e(TAG, "Error while adding appointment to Supabase $e", e)
            }


    override suspend fun getAppointmentsByUserId(
        userId: String,
    ): Result<List<AppointmentWithDetails>> =
        kotlin.runCatching {

            val query = AppointmentQuery(userId = userId)
            val response = supabaseApiService.getAppointmentWithDetails(query)

            if (response.isSuccessful) {
                val appointmentDtos = response.body() ?: emptyList()

                val appointmentsWithDetails = appointmentDtos.map {
                    appointmentMapper.appointmentWithDetailsDtoToAppointmentWithDetails(it)
                }

                val appointmentWithDetailsDbModels = appointmentsWithDetails.map {
                    appointmentMapper.appointmentWithDetailsToAppointmentWithDetailsDbModel(it)
                }
                withContext(Dispatchers.IO) {
                    vetClinicDao.insertAppointments(appointmentWithDetailsDbModels)
                }
                appointmentsWithDetails
            } else {
                throw Exception(
                    "Error fetching appointments: ${response.code()} " +
                            "- ${response.message()}"
                )
            }
        }.onFailure { e ->
            Log.e(TAG, "Error while fetching appointments", e)
            emptyList<AppointmentWithDetails>()
        }


//    override suspend fun getAppointmentsByDate(date: String, offset: Int, limit: Int) {
//
//        val response =
//            supabaseApiService.getAppointmentsWithDetailsByDate(date, offset, limit)
//        if (response.isSuccessful) {
//            val appointmentDtos = response.body() ?: emptyList()
//            val appointments = appointmentDtos.map {
//                appointmentMapper.appointmentWithDetailsDtoToAppointmentWithDetails(it)
//            }
//            Log.d(TAG, "appointments:  $appointments")
//        }
//    }


    @OptIn(ExperimentalPagingApi::class)
    override fun getAppointmentsByDate(
        date: String
    ): Flow<PagingData<AppointmentWithDetails>> {
        Log.d(TAG, "Getting appointments for date: $date")
        val pagingSourceFactory = { vetClinicDao.observeAppointmentsPaging(date) }

        val pager = Pager(
            config = PagingConfig(pageSize = 15),
            remoteMediator = AppointmentRemoteMediator(
                selectedDate = date,
                supabaseApiService = supabaseApiService,
                appointmentMapper = appointmentMapper,
                vetClinicDao = vetClinicDao
            ),
            pagingSourceFactory = pagingSourceFactory
        )
        Log.d(TAG, "Created pager for date: $date")
        return pager.flow.map { pagingData ->
            Log.d(TAG, "Mapping paging data")
            pagingData.map { dbModel ->
                appointmentMapper.appointmentWithDetailsDbModelToEntity(dbModel)
            }
        }
    }

//
//    override suspend fun getAppointmentsByDate(date: String): Result<List<AppointmentWithDetails>> =
//        kotlin.runCatching {
//            val startOfDay = "${date}T10:00:00"
//            val endOfDay = "${date}T20:00:00"
//
//            val response =
//                supabaseApiService.getAppointmentsByDate(
//                    startOfDay = "gte.$startOfDay",
//                    endOfDay = "lt.$endOfDay",
//                    page = 1,
//                    pageCount = 2
//                )
//            if (response.isSuccessful) {
//                val appointmentDtos = response.body() ?: throw Exception("Empty response body")
//                val appointments =
//                    appointmentDtos.map { appointmentMapper.appointmentDtoToAppointmentEntity(it) }
//
//                val appointmentsWithDetails = appointments.map { appointment ->
//                    getAppointmentWithDetailsForAdmin(appointment)
//                }
//                val appointmentWithDetailsDbModels = appointmentsWithDetails.map {
//                    appointmentMapper.appointmentWithDetailsToAppointmentWithDetailsDbModel(it)
//                }
//                vetClinicDao.insertAppointments(appointmentWithDetailsDbModels)
//
//                appointmentsWithDetails
//            } else {
//                throw Exception(
//                    "Error fetching appointments for admin: ${response.code()} " +
//                            "- ${response.message()}"
//                )
//            }
//        }
//            .onFailure { e ->
//                Log.e(TAG, "Error while fetching appointments for admin", e)
//                emptyList<AppointmentWithDetails>()
//            }


//    override suspend fun getAppointmentsByUserIdFromRoom(userId: String): Result<List<AppointmentWithDetails>> =
//        kotlin.runCatching {
//            withContext(Dispatchers.IO) {
//                val appointmentDbModels = vetClinicDao.getAppointmentsByUserId(userId)
//                if (appointmentDbModels.isEmpty()) {
//                    throw NoSuchElementException("Appointments with userID $userId not found in Room")
//                }
//
//                appointmentDbModels.map {
//                    appointmentMapper.appointmentWithDetailsDbModelToEntity(it)
//                }
//            }
//        }
//            .onFailure {
//                Log.e(TAG, "Error while getting appointments from Room", it)
//            }


    override fun observeAppointmentsFromRoom(userId: String): Flow<List<AppointmentWithDetails>> {
        return vetClinicDao.observeAppointmentsByUserId(userId).map { list ->
            list.map { appointmentMapper.appointmentWithDetailsDbModelToEntity(it) }
        }
    }

    override suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentWithDetails,
    ): Result<Unit> =
        kotlin.runCatching {

            val appointmentIdWithParam = "eq.{${updatedAppointment.id}}"

            val appointmentDto =
                appointmentMapper.appointmentWithDetailsEntityToAppointmentDto(updatedAppointment)
            val response =
                supabaseApiService.updateAppointmentStatus(appointmentIdWithParam, appointmentDto)

            if (!response.isSuccessful) {
                throw Exception("Error while updating appointment status in Supabase")
            }

            updateAppointmentStatusInRoom(updatedAppointment).getOrThrow()

        }.onFailure { e ->
            Log.e(TAG, e.message.toString())
        }


    override suspend fun updateAppointmentStatusInRoom(updatedAppointment: AppointmentWithDetails): Result<Unit> =
        kotlin.runCatching {
            vetClinicDao.updateAppointment(
                appointmentMapper.appointmentWithDetailsToAppointmentWithDetailsDbModel(
                    updatedAppointment
                )
            )
        }
            .onFailure { e ->
                Log.d(TAG, e.message.toString())
            }



    companion object {
        private const val TAG = "AppointmentRepositoryImpl"

    }
}