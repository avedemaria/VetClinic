package com.example.vetclinic.data.network

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

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = runBlocking {
            withTimeout(1000) {
                supabaseClient.auth.currentSessionOrNull()?.accessToken
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