package com.example.vetclinic.data.remoteSource.network

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor @Inject constructor(
    private val supabaseClient: SupabaseClient
): Interceptor {


    private var token: String? = supabaseClient.auth.currentSessionOrNull()?.accessToken


    override fun intercept(chain: Interceptor.Chain): Response {

        val session = supabaseClient.auth.currentSessionOrNull()
        Log.d("HeaderInterceptor", "$session")
        if (session?.refreshToken!= null) {
            token = runBlocking {
                withTimeout(3*60*1000) {
                    supabaseClient.auth.refreshCurrentSession()
                    supabaseClient.auth.currentSessionOrNull()?.accessToken
                }

        }
        }

        val request = chain.request().newBuilder().apply {

            if (token!=null) {

                addHeader(HEADER_AUTHORIZATION, "Bearer $token")
            }
        }.build()

        return chain.proceed(request)

    }


   private companion object {
         const val HEADER_AUTHORIZATION = "Authorization"
    }
}