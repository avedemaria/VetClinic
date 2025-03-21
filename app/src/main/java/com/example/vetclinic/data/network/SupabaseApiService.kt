package com.example.vetclinic.data.network

import androidx.room.Query
import com.example.vetclinic.data.network.model.AppointmentCreateDto
import com.example.vetclinic.data.network.model.AppointmentDto
import com.example.vetclinic.data.network.model.DepartmentDto
import com.example.vetclinic.data.network.model.DoctorDto
import com.example.vetclinic.data.network.model.PetDto
import com.example.vetclinic.data.network.model.ServiceDto
import com.example.vetclinic.data.network.model.TimeSlotDto
import com.example.vetclinic.data.network.model.UserDTO
import io.github.jan.supabase.auth.mfa.FactorType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path


interface SupabaseApiService {

    @GET("rest/v1/users")
    suspend fun getUserFromSupabaseDb(@retrofit2.http.Query("uid") userId: String):
            Response<List<UserDTO>>


    @GET("rest/v1/services")
    suspend fun getServiceFromSupabaseDbById(@retrofit2.http.Query("id") serviceId: String):
            Response<List<ServiceDto>>

    @GET("rest/v1/pets")
    suspend fun getPetsFromSupabaseDb(@retrofit2.http.Query("user_id") userId: String):
            Response<List<PetDto>>

    @POST("rest/v1/users")
    suspend fun addUser(@Body user: UserDTO): Response<Unit>

    @PATCH("rest/v1/users?uid=eq.{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body updatedUser: UserDTO
    ): Response<Unit>

    @PATCH("rest/v1/pets")
    suspend fun updatePet(
        @retrofit2.http.Query("pet_id") petId: String,
        @Body updatedPet: PetDto
    ): Response<Unit>

    @DELETE("rest/v1/pets")
    suspend fun deletePet(
        @retrofit2.http.Query("pet_id") petId: String
    ): Response<Unit>

    @POST("rest/v1/pets")
    suspend fun addPet(@Body petDto: PetDto): Response<Unit>


    @GET("rest/v1/departments?select=*")
    suspend fun getDepartments(): Response<List<DepartmentDto>>


    @GET("rest/v1/doctors?select=*")
    suspend fun getDoctors(): Response<List<DoctorDto>>


    @GET("rest/v1/services?select=*")
    suspend fun getServices(): Response<List<ServiceDto>>


    @GET("rest/v1/appointments")
    suspend fun getAppointments(
        @retrofit2.http.Query("select") select: String = "*, doctors: doctor_id(*),pets:pet_id(*)" +
                ",users:user_id(*),services:service_id(*)"
    ): Response<List<AppointmentDto>>


    @POST("rest/v1/appointments")
    suspend fun addAppointment(@Body appointmentCreateDto: AppointmentCreateDto): Response<Unit>


    @GET("rest/v1/time_slots?select=*")
    suspend fun getTimeSlots(): Response<List<TimeSlotDto>>


}




