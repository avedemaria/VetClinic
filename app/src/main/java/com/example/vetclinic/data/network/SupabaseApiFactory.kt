package com.example.vetclinic.data.network

import com.example.vetclinic.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SupabaseApiFactory {
    private const val BASE_URL = BuildConfig.SUPABASE_URL + "/rest/v1/"
    private const val API_KEY = BuildConfig.SUPABASE_KEY


    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val apiService = retrofit.create(SupabaseApiService::class.java)

}