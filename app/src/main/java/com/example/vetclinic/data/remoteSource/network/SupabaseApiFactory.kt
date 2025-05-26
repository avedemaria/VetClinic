package com.example.vetclinic.data.remoteSource.network

import com.example.vetclinic.BuildConfig
import com.example.vetclinic.data.remoteSource.network.model.AuthInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SupabaseApiFactory @Inject constructor(
    authInterceptor: AuthInterceptor
) {

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SUPABASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val apiService: SupabaseApiService = retrofit.create(SupabaseApiService::class.java)


//    private val loggingInterceptor by lazy {
//        HttpLoggingInterceptor().apply {
//            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
//            else HttpLoggingInterceptor.Level.NONE
//        }
//    }
//
//    private val authInterceptor by lazy {
//        Interceptor { chain ->
//            val original = chain.request()
//            val token = runBlocking {
//                getValidAccessToken()
//            }
//            val requestBuilder = original.newBuilder().addHeader("apikey", BuildConfig.SUPABASE_KEY)
////                .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_KEY}")
//
//            if (!token.isNullOrEmpty()) {
//                requestBuilder.addHeader("Authorization", "Bearer $token")
//            }
//
//            val request = requestBuilder.method(original.method, original.body).build()
//            chain.proceed(request)
//        }
//    }
//
//
//    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
//        .addInterceptor(loggingInterceptor).addInterceptor(authInterceptor)
//        .addInterceptor(headerInterceptor).build()
//
//
//    private val moshi =
//        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//
//    private val retrofit by lazy {
//        Retrofit.Builder().client(client).addConverterFactory(MoshiConverterFactory.create(moshi))
//            .baseUrl(BuildConfig.SUPABASE_URL)
//            .build()
//    }
//
//    val apiService: SupabaseApiService = retrofit.create(SupabaseApiService::class.java)
//
//
//    private suspend fun getValidAccessToken(): String? {
//        var currentToken = userDataStore.getAccessToken()
//
//        if (!currentToken.isNullOrEmpty() && isTokenExpired(currentToken)) {
//            try {
//                val refreshToken = userDataStore.getRefreshToken()
//                Log.d("TokenRefresh", "Checking refresh token: $refreshToken")
//
//                if (!refreshToken.isNullOrEmpty()) {
//                    Log.d("TokenRefresh", "Attempting to refresh session...")
//                    val newSession = supabaseClient.auth.refreshSession(refreshToken)
//                    val newAccessToken = newSession.accessToken
//
//                    if (!newAccessToken.isNullOrEmpty()) {
//                        Log.d("TokenRefresh", "Successfully refreshed token: $newAccessToken")
//                        userDataStore.saveAccessToken(newAccessToken)
//                        userDataStore.saveRefreshToken(newSession.refreshToken ?: refreshToken)
//                        currentToken = newAccessToken
//                    } else {
//                        Log.e("TokenRefresh", "New access token is null after refresh.")
//                        throw IllegalStateException("New token is null")
//                    }
//                } else {
//                    Log.e("TokenRefresh", "Refresh token is null or empty.")
//                    throw IllegalStateException("Refresh token is missing")
//                }
//            } catch (e: Exception) {
//                Log.e("TokenRefresh", "Failed to refresh session", e)
//                throw e
//            }
//        } else {
//            Log.d("TokenRefresh", "Access token not expired or missing: $currentToken")
//        }
//
//        return currentToken
//    }
//
//    private fun isTokenExpired(token: String): Boolean {
//        return try {
//            val parts = token.split(".")
//            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
//            val payload = JSONObject(payloadJson)
//            val exp = payload.getLong("exp") * 1000
//            val now = System.currentTimeMillis()
//            exp < now
//        } catch (e: Exception) {
//            true
//        }
//    }

}