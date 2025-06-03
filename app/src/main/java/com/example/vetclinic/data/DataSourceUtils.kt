package com.example.vetclinic.data

import retrofit2.Response
import timber.log.Timber


const val TAG = "DataSourceUtils"

object DataSourceUtils {

    suspend fun <T> executeRoomCall(call: suspend () -> T): Result<T> = runCatching {
        call()
    }.onFailure { e ->
        Timber.tag(TAG).e(e, "Room operation failed: ${e.message}")
    }

    suspend fun <T> executeApiCall(call: suspend () -> Response<T>): Result<T> =
        runCatching { call() }.mapCatching { response ->
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                val error = response.errorBody()?.string()
                throw Exception("HTTP ${response.code()}: $error")
            }
        }
}

