package com.example.vetclinic.data.network

import com.example.vetclinic.data.network.model.UserDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface SupabaseApiService {

    @GET("users?uid=eq.{userId}")
    suspend fun getCurrentUser(@Path("userId") userId: String): UserDTO?


    @POST ("users")
    suspend fun addUser (@Body user: UserDTO)

}


