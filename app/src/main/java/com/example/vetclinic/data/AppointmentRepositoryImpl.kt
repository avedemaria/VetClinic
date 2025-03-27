package com.example.vetclinic.data

import android.util.Log
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.mapper.ServiceMapper
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.AppointmentRepository
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.Service
import com.example.vetclinic.domain.entities.User
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext


class AppointmentRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val vetClinicDao: VetClinicDao,
    private val appointmentMapper: AppointmentMapper,
    private val doctorMapper: DoctorMapper,
    private val serviceMapper: ServiceMapper,
    private val userMapper: UserMapper,
    private val petMapper: PetMapper
) : AppointmentRepository {

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


    override suspend fun getAppointmentsByUserId(userId: String): Result<List<AppointmentWithDetails>> =
        kotlin.runCatching {

            val userIdWithParam = "eq.$userId"

            val response = supabaseApiService.getAppointmentsByUserId(userIdWithParam)
            if (response.isSuccessful) {
                val appointmentDtos = response.body() ?: throw Exception("Empty response body")
                val appointments =
                    appointmentDtos.map { appointmentMapper.appointmentDtoToAppointmentEntity(it) }

                coroutineScope {
                    appointments.map { appointment ->
                        val serviceDeferred = async { getServiceById(appointment.serviceId) }
                        val doctorDeferred = async { getDoctorById(appointment.doctorId) }
                        val petDeferred = async { getPetFromRoomById(appointment.petId) }
                        val userDeferred = async { getUserFromRoomById(appointment.userId) }

                        val service = serviceDeferred.await()
                        val doctor = doctorDeferred.await()
                        val pet = petDeferred.await()
                        val user = userDeferred.await()

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
            } else {
                throw Exception(
                    "Error fetching appointments: ${response.code()}" +
                            " - ${response.message()}"
                )
            }
        }.onFailure { e ->
            Log.e(TAG, "Error while fetching appointments" ,e)
        }


    override suspend fun getDoctorById(doctorId: String): Doctor = withContext(Dispatchers.IO) {
        val doctorIdWithParam = "eq.$doctorId"

        try {
            val response = supabaseApiService.getDoctorFromSupabaseDbById(doctorIdWithParam)
            if (response.isSuccessful) {
                val doctorDtos = response.body() ?: throw Exception("Empty response body")
                val doctor = doctorMapper.doctorDtoListToDoctorEntityList(doctorDtos).firstOrNull()
                doctor ?: throw Exception("Doctor not found")
            } else {
                throw Exception("Error while fetching doctors fromSupabase")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to get doctor by ID: ${e.message}")
            throw Exception("Failed to get doctor by ID: ${e.message}")
        }
    }

    override suspend fun getServiceById(serviceId: String): Service = withContext(Dispatchers.IO) {

        val serviceIdWithParam = "eq.$serviceId"

        try {
            val response = supabaseApiService.getServiceFromSupabaseDbById(serviceIdWithParam)
            if (response.isSuccessful) {
                val serviceDtos = response.body() ?: throw Exception("Empty response body")
                val service =
                    serviceMapper.serviceDtoListToServiceEntityList(serviceDtos).firstOrNull()
               service ?: throw Exception("Service not found")
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