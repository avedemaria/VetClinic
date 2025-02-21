package com.example.vetclinic.data.network

import com.example.vetclinic.BuildConfig
import com.example.vetclinic.CodeReview
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseApiFactory {
    private const val BASE_URL = BuildConfig.SUPABASE_URL
    private const val API_KEY = BuildConfig.SUPABASE_KEY


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        @CodeReview("Логи не должны быть в проде (там даже ключ будет печататься сейчас)")
       // level = HttpLoggingInterceptor.Level.BODY
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @CodeReview("Раньше создавался новый билдер, но не копировалось метод и тело запроса. Пост может сломаться")
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .addHeader("apikey", API_KEY)
            .addHeader("Authorization", "Bearer $API_KEY")
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }

    @CodeReview("Обязательно таймауты, чтобы не было вечного зависания")
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(SingleUserAdapter())
        .build()

    @CodeReview("Можно добавить lazy, чтобы создавался при использовании, а не при запуске")
    private val retrofit by lazy {
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .build()
    }

//    private val retrofit = Retrofit.Builder()
//        .client(client)
//        .addConverterFactory(MoshiConverterFactory.create(moshi))
//        .baseUrl(BASE_URL)
//        .build()

    val apiService = retrofit.create(SupabaseApiService::class.java)

}