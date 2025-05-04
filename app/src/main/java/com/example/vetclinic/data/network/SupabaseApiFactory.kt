package com.example.vetclinic.data.network

import com.example.vetclinic.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SupabaseApiFactory @Inject constructor(
    headerInterceptor: HeaderInterceptor
) {
    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
    }

    private val authInterceptor by lazy {
        Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder().addHeader("apikey", BuildConfig.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_KEY}")
                .method(original.method, original.body).build()
            chain.proceed(request)
        }
    }


    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor).addInterceptor(headerInterceptor)
        .addInterceptor(authInterceptor).build()


    private val moshi =
        Moshi.Builder().add(KotlinJsonAdapterFactory()).add(SingleUserAdapter()).build()

    private val retrofit by lazy {
        Retrofit.Builder().client(client).addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BuildConfig.SUPABASE_URL)
            // supabase sdk injection and observing retrofit requests
            .build()
    }

    val apiService: SupabaseApiService = retrofit.create(SupabaseApiService::class.java)


}