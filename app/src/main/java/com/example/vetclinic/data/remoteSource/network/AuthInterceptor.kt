package com.example.vetclinic.data.remoteSource.network

import android.util.Log
import com.example.vetclinic.BuildConfig
import com.example.vetclinic.domain.repository.UserDataStore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject


class AuthInterceptor @Inject constructor(
    private val userDataStore: UserDataStore,
    private val supabaseClient: SupabaseClient
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = runBlocking {
            getValidAccessToken()
        }
        val requestBuilder = original.newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_KEY)

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.method(original.method, original.body).build()
        return chain.proceed(request)
    }

    private suspend fun getValidAccessToken(): String? {
        var currentToken = userDataStore.getAccessToken()

        if (!currentToken.isNullOrEmpty() && isTokenExpired(currentToken)) {
            try {
                val refreshToken = userDataStore.getRefreshToken()

                if (!refreshToken.isNullOrEmpty()) {
                    val newSession = withTimeout(30_000) {
                        supabaseClient.auth.refreshSession(refreshToken)
                    }

                    val newAccessToken = newSession.accessToken
                    if (newAccessToken.isNotEmpty()) {
                        userDataStore.saveAccessToken(newAccessToken)
                        userDataStore.saveRefreshToken(newSession.refreshToken)
                        currentToken = newAccessToken
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Token refresh failed", e)
            }
        }

        return currentToken
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
            val payload = JSONObject(payloadJson)
            val exp = payload.getLong("exp") * 1000
            val now = System.currentTimeMillis()
            exp < now
        } catch (e: Exception) {
            true
        }
    }

    companion object {
        private const val TAG = "AuthInterceptor"
    }
}