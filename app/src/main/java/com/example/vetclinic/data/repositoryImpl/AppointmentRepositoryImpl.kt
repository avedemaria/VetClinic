package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.mapper.ServiceMapper
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.data.network.model.AppointmentDto
import com.example.vetclinic.domain.AppointmentRepository
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.Service
import com.example.vetclinic.domain.entities.User
import com.squareup.moshi.Moshi
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


class AppointmentRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val supabaseClient: SupabaseClient,
    private val vetClinicDao: VetClinicDao,
    private val appointmentMapper: AppointmentMapper,
    private val doctorMapper: DoctorMapper,
    private val serviceMapper: ServiceMapper,
    private val userMapper: UserMapper,
    private val petMapper: PetMapper,
    private val moshi: Moshi,
) : AppointmentRepository {


    private val _appointmentsUpdates = MutableSharedFlow<AppointmentWithDetails>(replay = 1)
    val appointmentsUpdates: SharedFlow<AppointmentWithDetails> =
        _appointmentsUpdates.asSharedFlow()


    private var subscription: RealtimeChannel? = null

    private val jsonAdapter = moshi.adapter(AppointmentDto::class.java)


//    override suspend fun subscribeToAppointmentChanges(
//        callback: (Appointment) -> Unit,
//    ) {
//        val channel = supabaseClient.channel("appointments")
//        val changeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
//            table = "appointments"
//        }
//        subscription = channel
//        channel.subscribe()
//        Log.d(TAG, "Supabase channel subscribed successfully")
//
//        Log.d(TAG, "Starting to collect changes from Supabase")
//        changeFlow.collect { updatedValue ->
//            val updatedRecord = updatedValue.record.toString()
//
//            try {
//                val appointmentDto = jsonAdapter.fromJson(updatedRecord)
//                appointmentDto?.let { dto ->
//                    callback(appointmentMapper.appointmentDtoToAppointmentEntity(dto))
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Ошибка парсинга JSON: ${e.message}")
//            }
//
//
//        }
//    }
//

    override suspend fun subscribeToAppointmentChanges(): Flow<Appointment> {
        val channel = supabaseClient.channel("appointments")
        val changeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "appointments"
        }
        channel.subscribe()
        Log.d(TAG, "Supabase channel subscribed successfully")

        return flow {
            changeFlow.collect { updatedValue ->
                val updatedRecord = updatedValue.record.toString()

                try {
                    val appointmentDto = jsonAdapter.fromJson(updatedRecord)
                    appointmentDto?.let { dto ->
                        emit(appointmentMapper.appointmentDtoToAppointmentEntity(dto))
                        // Emit изменения через Flow
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка парсинга JSON: ${e.message}")
                }
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

            val userIdWithParam = "eq.$userId"
//            val isArchivedWithParam = "eq.$isArchived"

            val response = supabaseApiService.getAppointmentsByUserId(
                userIdWithParam
            )

            if (response.isSuccessful) {
                val appointmentDtos = response.body() ?: throw Exception("Empty response body")

                val appointments =
                    appointmentDtos.map { appointmentMapper.appointmentDtoToAppointmentEntity(it) }
                val appointmentsWithDetails = appointments.map { appointmentEntity ->
                    getAppointmentWithDetails(appointmentEntity)
                }

                val appointmentWithDetailsDbModels = appointmentsWithDetails.map {
                    appointmentMapper.appointmentWithDetailsToAppointmentWithDetailsDbModel(it)
                }

                withContext(Dispatchers.IO) {
                    vetClinicDao.insertAppointments(appointmentWithDetailsDbModels)
                    val appointmentsInDb = vetClinicDao.getAppointmentsByUserId(userId)
                    Log.d(TAG, "Appointments in Room after insert: $appointmentsInDb")
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


    override suspend fun getAppointmentsByDate(date: String): Result<List<AppointmentWithDetails>> =
        kotlin.runCatching {
            val startOfDay = "${date}T10:00:00"
            val endOfDay = "${date}T20:00:00"

            val response =
                supabaseApiService.getAppointmentsByDate(
                    "gte.$startOfDay",
                    "lt.$endOfDay"
                )
            if (response.isSuccessful) {
                val appointmentDtos = response.body() ?: throw Exception("Empty response body")
                val appointments =
                    appointmentDtos.map { appointmentMapper.appointmentDtoToAppointmentEntity(it) }

                appointments.map { appointment ->
                    getAppointmentWithDetails(appointment)
                }
            } else {
                throw Exception(
                    "Error fetching appointments for admin: ${response.code()} " +
                            "- ${response.message()}"
                )
            }
        }
            .onFailure { e ->
                Log.e(TAG, "Error while fetching appointments for admin", e)
                emptyList<AppointmentWithDetails>()
            }


    override suspend fun getAppointmentsByUserIdFromRoom(userId: String): Result<List<AppointmentWithDetails>> =
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                val appointmentDbModels = vetClinicDao.getAppointmentsByUserId(userId)
                if (appointmentDbModels.isEmpty()) {
                    throw NoSuchElementException("Appointments with userID $userId not found in Room")
                }

                appointmentDbModels.map {
                    appointmentMapper.appointmentWithDetailsDbModelToEntity(it)
                }
            }
        }
            .onFailure {
                Log.e(TAG, "Error while getting appointments from Room", it)
            }


    override suspend fun updateAppointmentStatus(
        updatedAppointment: AppointmentWithDetails,
    ): Result<Unit> =
        kotlin.runCatching {

            val appointmentIdWithParam = "eq.{${updatedAppointment.id}}"
            val appointment =
                appointmentMapper.appointmentWithDetailsToAppointment(updatedAppointment)
            val appointmentDto =
                appointmentMapper.appointmentEntityToAppointmentDto(appointment)
            val response =
                supabaseApiService.updateAppointmentStatus(appointmentIdWithParam, appointmentDto)

            vetClinicDao.updateAppointment(
                appointmentMapper.appointmentWithDetailsToAppointmentWithDetailsDbModel(
                    updatedAppointment
                )
            )
            if (!response.isSuccessful) {
                throw Exception("Error while updating appointment status in Supabase")
            }
        }.onFailure { e ->
            Log.e(TAG, e.message.toString())
        }

    private suspend fun getAppointmentWithDetails(appointment: Appointment): AppointmentWithDetails {

        return coroutineScope {
            val serviceDeferred = async { getServiceById(appointment.serviceId) }
            val doctorDeferred = async { getDoctorById(appointment.doctorId) }
            val petDeferred = async { getPetFromRoomById(appointment.petId) }
            val userDeferred = async { getUserFromRoomById(appointment.userId) }


            val service = serviceDeferred.await()
            val doctor = doctorDeferred.await()
            val pet = petDeferred.await()
            val user = userDeferred.await()

            Log.d(TAG, "Loaded pet: $pet")
            Log.d(TAG, "Loaded user: $user")

            appointmentMapper.appointmentToAppointmentWithDetails(
                appointment = appointment,
                serviceName = service.serviceName,
                doctorName = doctor.doctorName,
                doctorRole = doctor.role,
                userName = "${user.userName} ${user.userLastName}",
                petName = pet.petName
            )
        }

    }


    override suspend fun getDoctorById(doctorId: String): Doctor {
        val doctorIdWithParam = "eq.$doctorId"

        try {
            val response = supabaseApiService.getDoctorFromSupabaseDbById(doctorIdWithParam)
            if (response.isSuccessful) {
                val doctorDtos = response.body() ?: throw Exception("Empty response body")
                val doctor = doctorMapper.doctorDtoListToDoctorEntityList(doctorDtos).firstOrNull()
                return doctor ?: throw Exception("Doctor not found")
            } else {
                throw Exception("Error while fetching doctors fromSupabase")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to get doctor by ID: ${e.message}")
            throw Exception("Failed to get doctor by ID: ${e.message}")
        }
    }

    override suspend fun getServiceById(serviceId: String): Service {

        val serviceIdWithParam = "eq.$serviceId"

        try {
            val response = supabaseApiService.getServiceFromSupabaseDbById(serviceIdWithParam)
            if (response.isSuccessful) {
                val serviceDtos = response.body() ?: throw Exception("Empty response body")
                val service =
                    serviceMapper.serviceDtoListToServiceEntityList(serviceDtos).firstOrNull()
                return service ?: throw Exception("Service not found")
            } else {
                throw Exception("Error while fetching services fromSupabase")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to get service by ID: ${e.message}")
            throw Exception("Failed to get service by ID: ${e.message}")
        }

    }

    override suspend fun getPetFromRoomById(petId: String): Pet = withContext(Dispatchers.IO) {
        try {
            val petDbModel = vetClinicDao.getPetById(petId)
            Log.d(TAG, "Fetching pet with ID: ${petDbModel.petId}")
            petMapper.petDbModelToPetEntity(petDbModel)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to get pet by ID: ${e.message}")
            throw Exception("Failed to get pet by ID: ${e.message}")
        }
    }

    override suspend fun getUserFromRoomById(userId: String): User = withContext(Dispatchers.IO) {
        try {
            val userDbModel = vetClinicDao.getUserById(userId)
            userMapper.userDbModelToUserEntity(
                userDbModel ?: throw Exception("User not found in room")
            )
        } catch (e: Exception) {
            Log.d(TAG, "Failed to get user by ID: ${e.message}")
            throw Exception("Failed to get user by ID: ${e.message}")
        }
    }


    companion object {
        private const val TAG = "AppointmentRepositoryImpl"
    }
}