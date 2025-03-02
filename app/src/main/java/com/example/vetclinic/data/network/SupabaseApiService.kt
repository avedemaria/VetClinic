package com.example.vetclinic.data.network

import androidx.room.Query
import com.example.vetclinic.data.network.model.DepartmentDto
import com.example.vetclinic.data.network.model.DoctorDto
import com.example.vetclinic.data.network.model.PetDto
import com.example.vetclinic.data.network.model.ServiceDto
import com.example.vetclinic.data.network.model.UserDTO
import io.github.jan.supabase.auth.mfa.FactorType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface SupabaseApiService {

    @GET("rest/v1/users?uid=eq.{userId}")
    suspend fun getUserFromSupabaseDb(@Path("userId") userId: String): Response<List<UserDTO?>>

    @POST("rest/v1/users")
    suspend fun addUser(@Body user: UserDTO): Response<Unit>

    @POST("rest/v1/pets")
    suspend fun addPet(@Body petDto: PetDto): Response<Unit>


    @GET("rest/v1/departments?select=*")
    suspend fun getDepartments (): Response<List<DepartmentDto>>


    @GET("rest/v1/doctors?select=*")
    suspend fun getDoctors (): Response<List<DoctorDto>>


    @GET("rest/v1/services?select=*")
    suspend fun getServices(): Response<List<ServiceDto>>


}




