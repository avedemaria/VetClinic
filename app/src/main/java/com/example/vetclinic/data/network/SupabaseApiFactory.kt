package com.example.vetclinic.data.network

import android.util.Log
import com.example.vetclinic.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SupabaseApiFactory {
    private const val BASE_URL = BuildConfig.SUPABASE_URL
    private const val API_KEY = BuildConfig.SUPABASE_KEY


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request()


            Log.d("Request", "Запрос: ${request.method} ${request.url}")
            Log.d("Request", "Заголовки: ${request.headers}")


            request.body?.let {
                val buffer = okio.Buffer()
                it.writeTo(buffer)
                Log.d("Request", "Тело запроса: ${buffer.readUtf8()}")  // Логируем строку тела
            } ?: Log.d("Request", "Тело запроса: нет")


            val newRequest = request.newBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer $API_KEY")
                .build()

            return@addInterceptor chain.proceed(newRequest)
        }
        .build()



//    private val client = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .addInterceptor { chain ->
//            val request = chain.request()
//
//                .newBuilder()
//                .addHeader("apikey", API_KEY)
//                .addHeader("Authorization", "Bearer $API_KEY")
//                .build()
//            chain.proceed(request)
//        }
//        .build()

    private val moshi = Moshi.Builder().add(SingleUserAdapter())
        .build()

    private val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    val apiService = retrofit.create(SupabaseApiService::class.java)

}