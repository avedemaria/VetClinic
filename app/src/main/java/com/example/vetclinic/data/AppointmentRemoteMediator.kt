//package com.example.vetclinic.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.vetclinic.data.database.model.AppointmentWithDetailsDbModel
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.mapper.ServiceMapper
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalPagingApi::class)
class AppointmentRemoteMediator @Inject constructor(
    private val selectedDate: String,
    private val vetClinicDao: VetClinicDao,
    private val supabaseApiService: SupabaseApiService,
    private val appointmentMapper: AppointmentMapper,
    private val petMapper: PetMapper,
    private val userMapper: UserMapper,
    private val doctorMapper: DoctorMapper,
    private val serviceMapper: ServiceMapper,

    ) : RemoteMediator<Int, AppointmentWithDetailsDbModel>() {


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AppointmentWithDetailsDbModel>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) 1 else (lastItem.id.toInt() / state.config.pageSize) + 1
                }
            }


            val startOfDay = "${selectedDate}T10:00:00"

            val dateTimeFilterStart = "gte.$startOfDay"

            val response = supabaseApiService.getAppointmentsByDate(
                dateTime = dateTimeFilterStart,
                page = page,
                pageCount = state.config.pageSize
            )
            if (response.isSuccessful) {
                val appointmentDtos = response.body().orEmpty()
                val appointmentEntities =
                    appointmentDtos.map { appointmentMapper.appointmentDtoToAppointmentEntity(it) }
                val appointmentWithDetails = appointmentEntities.map {
                    getAppointmentWithDetailsForAdmin(it)
                }
                val appointmentWithDetailsDbModels = appointmentWithDetails.map {
                    appointmentMapper.appointmentWithDetailsToAppointmentWithDetailsDbModel(it)
                }

                vetClinicDao.insertAppointments(appointmentWithDetailsDbModels)

                MediatorResult.Success(endOfPaginationReached = appointmentDtos.isEmpty())
            } else {
                MediatorResult.Error(
                    Exception(
                        "Server's error: ${response.code()} " +
                                "- ${response.message()}"
                    )
                )
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

    }


    private suspend fun getAppointmentWithDetailsForAdmin(
        appointment: Appointment,
    ): AppointmentWithDetails {

        return coroutineScope {
            val serviceDeferred =
                async { supabaseApiService.getServiceFromSupabaseDbById(appointment.serviceId) }
            val doctorDeferred =
                async { supabaseApiService.getDoctorFromSupabaseDbById(appointment.doctorId) }
            val petDeferred =
                async { supabaseApiService.getPetFromSupabaseDbById(appointment.petId) }
            val userDeferred =
                async { supabaseApiService.getUserFromSupabaseDbById(appointment.userId) }

            val service = serviceDeferred.await().body()?.firstOrNull()
                ?.let { serviceMapper.serviceDtoToServiceEntity(it) }
            val doctor = doctorDeferred.await().body()?.firstOrNull()
                ?.let { doctorMapper.doctorDtoToDoctorEntity(it) }
            val pet =
                petDeferred.await().body()?.firstOrNull()?.let { petMapper.petDtoToPetEntity(it) }
            val user =
                userDeferred.await().body()?.firstOrNull()
                    ?.let { userMapper.userDtoToUserEntity(it) }



            if (service == null || doctor == null || pet == null || user == null) {
                throw Exception("One or more required data were not fetched successfully")
            }

            appointmentMapper.appointmentToAppointmentWithDetails(
                appointment = appointment,
                serviceName = service.serviceName,
                doctorName = doctor.doctorName,
                doctorRole = doctor.role,
                userName = "${user.userName} ${user.userLastName}",
                petName = pet.petName,
                petAge = pet.petAge.toString()
            )
        }
    }


}




