package com.example.vetclinic.data.network

import androidx.room.Query
import com.example.vetclinic.data.network.model.DoctorDto
import com.example.vetclinic.data.network.model.PetDto
import com.example.vetclinic.data.network.model.UserDTO
import io.github.jan.supabase.auth.mfa.FactorType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface SupabaseApiService {

    @GET("users?uid=eq.{userId}")
    suspend fun getCurrentUser(@Path("userId") userId: String): UserDTO?


    @POST("rest/v1/users")
    suspend fun addUser(@Body user: UserDTO): Response<Unit>

    @POST("rest/v1/pets")
    suspend fun addPet(@Body petDto: PetDto): Response<Unit>


    @GET("rest/v1/doctors?select=*")
    suspend fun getDoctors (): Response<List<DoctorDto>>



}




